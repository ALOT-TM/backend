package com.fluxusbackend.authaccess.interfaces.rest.transform;

import com.fluxusbackend.authaccess.application.internal.services.AuthorizationService;
import com.fluxusbackend.authaccess.application.internal.services.RetailFullAccessRoleService;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.companyretail.infrastructure.persistence.jpa.repositories.RetailCompanyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/auth/roles")
@Tag(name = "Roles Management", description = "CRUD operations on roles")
@SecurityRequirement(name = "bearer")
public class RoleController {

    private final RetailCompanyRepository retailCompanyRepository;
    private final AuthorizationService authorizationService;
    private final RetailFullAccessRoleService retailFullAccessRoleService;

    public RoleController(RetailCompanyRepository retailCompanyRepository,
                          AuthorizationService authorizationService,
                          RetailFullAccessRoleService retailFullAccessRoleService) {
        this.retailCompanyRepository = retailCompanyRepository;
        this.authorizationService = authorizationService;
        this.retailFullAccessRoleService = retailFullAccessRoleService;
    }

    @GetMapping
    @Operation(summary = "List roles for current retail company")
    public List<RoleDto> listRoles() {
        authorizationService.requireActor(UserActor.RETAIL);
        var role = resolveDefaultRoleForCurrentCompany();
        return List.of(new RoleDto(role.getRoleId(), role.getName()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Resolve default full access role for current company")
    public RoleDto createRole(@RequestBody CreateRolePayload payload) {
        authorizationService.requireActor(UserActor.RETAIL);
        var role = resolveDefaultRoleForCurrentCompany();
        return new RoleDto(role.getRoleId(), role.getName());
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Resolve default full access role")
    public RoleDto updateRole(@PathVariable Long roleId, @RequestBody CreateRolePayload payload) {
        authorizationService.requireActor(UserActor.RETAIL);
        var role = resolveDefaultRoleForCurrentCompany();
        return new RoleDto(role.getRoleId(), role.getName());
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete role")
    public void deleteRole(@PathVariable Long roleId) {
        authorizationService.requireActor(UserActor.RETAIL);
        resolveDefaultRoleForCurrentCompany();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RETAIL_FULL_ACCESS cannot be deleted");
    }

    private com.fluxusbackend.authaccess.domain.model.aggregates.Role resolveDefaultRoleForCurrentCompany() {
        Long companyId = authorizationService.getCurrentUserCompanyId().value();
        var company = retailCompanyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return retailFullAccessRoleService.resolveForCompany(company);
    }

    public record RoleDto(Long roleId, String name) {}
    public record CreateRolePayload(String name) {}
}
