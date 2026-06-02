package com.fluxusbackend.authaccess.interfaces.rest.transform;

import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import com.fluxusbackend.authaccess.domain.model.commands.RegisterUserCommand;
import com.fluxusbackend.authaccess.domain.model.dto.AuthenticatedUser;
import com.fluxusbackend.authaccess.domain.model.dto.Profile;
import com.fluxusbackend.authaccess.domain.model.dto.UserAccountDto;
import com.fluxusbackend.authaccess.domain.model.queries.GetUserByIdQuery;
import com.fluxusbackend.authaccess.domain.model.queries.LoginUserQuery;
import com.fluxusbackend.authaccess.domain.model.valueobjects.UserId;
import com.fluxusbackend.authaccess.domain.services.UserAuthenticationQueryService;
import com.fluxusbackend.authaccess.domain.services.UserCommandService;
import com.fluxusbackend.authaccess.domain.services.UserQueryService;
import com.fluxusbackend.shared.application.security.AuthenticatedUserPrincipal;
import com.fluxusbackend.shared.application.security.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth & Access", description = "User registration and login")
public class AuthAccessController {

    private final UserCommandService userCommandService;
    private final UserAuthenticationQueryService userAuthenticationQueryService;
    private final JwtTokenService jwtTokenService;
    private final UserQueryService userQueryService;
    private final com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.UserAccountRepository userAccountRepository;
    private final com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.RoleRepository roleRepository;
    private final com.fluxusbackend.authaccess.application.internal.services.AuthorizationService authorizationService;

    public AuthAccessController(
            UserCommandService userCommandService,
            UserAuthenticationQueryService userAuthenticationQueryService,
            JwtTokenService jwtTokenService,
            UserQueryService userQueryService,
            com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.UserAccountRepository userAccountRepository,
            com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.RoleRepository roleRepository,
            com.fluxusbackend.authaccess.application.internal.services.AuthorizationService authorizationService
    ) {
        this.userCommandService = userCommandService;
        this.userAuthenticationQueryService = userAuthenticationQueryService;
        this.jwtTokenService = jwtTokenService;
        this.userQueryService = userQueryService;
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered",
                    content = @Content(schema = @Schema(implementation = UserAccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public UserAccountDto register(@Valid @RequestBody RegisterUserCommand command) {
        var user = userCommandService.handle(command);
        return UserAccountDto.from(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthenticatedUser.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    public AuthenticatedUser login(@Valid @RequestBody LoginUserQuery query) {
        var user = userAuthenticationQueryService.handle(query);
        var token = jwtTokenService.generateToken(user);
        return new AuthenticatedUser(UserAccountDto.from(user), token);
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "bearer")
    public Profile profile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            return new Profile(null, null, null, null, null);
        }
        var companyId = principal.companyId();
        Long companyLong = companyId == null ? null : companyId.value();
        return new Profile(
                companyLong,
                principal.beneficiaryInstitutionId(),
                principal.email(),
                principal.actor().name(),
                principal.roleName()
        );
    }

    @GetMapping("/users/{userId}")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Get user account by id (no caller validation)")
    public UserAccountDto getUserById(@PathVariable Long userId) {
        var user = userQueryService.handle(new GetUserByIdQuery(new UserId(userId)))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserAccountDto.from(user);
    }

    @GetMapping("/users")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "List users")
    public List<UserAccountDto> listUsers() {
        List<UserAccount> users = userQueryService.findAll();
        var dtos = new ArrayList<UserAccountDto>();
        for (var u : users) dtos.add(UserAccountDto.from(u));
        return dtos;
    }

    @PutMapping("/profile")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Update current user profile")
    public UserAccountDto updateProfile(@Valid @RequestBody UpdateProfilePayload payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
        }
        var command = new com.fluxusbackend.authaccess.domain.model.commands.UpdateProfileCommand(principal.userId(), payload.username(), payload.email());
        var user = userCommandService.handle(command);
        return UserAccountDto.from(user);
    }

    public record UpdateProfilePayload(String username, String email) {}

    @PutMapping("/change-password")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Change current user password")
    public void changePassword(@Valid @RequestBody ChangePasswordPayload payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
        }
        var command = new com.fluxusbackend.authaccess.domain.model.commands.ChangePasswordCommand(principal.userId(), payload.currentPassword(), payload.newPassword());
        userCommandService.handle(command);
    }

    public record ChangePasswordPayload(String currentPassword, String newPassword) {}

    @PutMapping("/users/{userId}/role")
    @SecurityRequirement(name = "bearer")
    @PreAuthorize("hasRole('RETAIL_MANAGER')")
    @Operation(summary = "Update user role (MANAGER only)")
    @Transactional
    public UserAccountDto updateUserRole(@PathVariable Long userId, @Valid @RequestBody UpdateUserRolePayload payload) {
        Long managerCompanyId = authorizationService.getCurrentUserCompanyId().value();

        var user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var retailUser = user.getRetailUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a retail user"));

        // Cross-tenant validation: Manager and target user must belong to the same company
        if (!retailUser.getRetailCompany().getRetailCompanyId().equals(managerCompanyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Manager and target user company mismatch");
        }

        var role = roleRepository.findById(payload.roleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        // Validation: New role must belong to the manager's company
        if (!role.getRetailCompany().getRetailCompanyId().equals(managerCompanyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Role does not belong to this company");
        }

        retailUser.updateRole(role);
        var saved = userAccountRepository.save(user);
        return UserAccountDto.from(saved);
    }

    public record UpdateUserRolePayload(Long roleId) {}
}
