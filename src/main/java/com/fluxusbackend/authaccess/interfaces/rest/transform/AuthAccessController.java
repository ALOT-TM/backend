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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth & Access", description = "User registration and login")
public class AuthAccessController {

    private final UserCommandService userCommandService;
    private final UserAuthenticationQueryService userAuthenticationQueryService;
                private final JwtTokenService jwtTokenService;
        private final UserQueryService userQueryService;

        public AuthAccessController(
            UserCommandService userCommandService,
                        UserAuthenticationQueryService userAuthenticationQueryService,
                        JwtTokenService jwtTokenService,
                        UserQueryService userQueryService
    ) {
        this.userCommandService = userCommandService;
        this.userAuthenticationQueryService = userAuthenticationQueryService;
                this.jwtTokenService = jwtTokenService;
        this.userQueryService = userQueryService;
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
             public java.util.List<UserAccountDto> listUsers() {
                        java.util.List<UserAccount> users = userQueryService.findAll();
                        var dtos = new java.util.ArrayList<UserAccountDto>();
                        for (var u : users) dtos.add(UserAccountDto.from(u));
                        return dtos;
        }
}
