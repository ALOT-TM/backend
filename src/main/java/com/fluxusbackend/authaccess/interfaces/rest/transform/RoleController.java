package com.fluxusbackend.authaccess.interfaces.rest.transform;

import com.fluxusbackend.authaccess.application.internal.services.AuthorizationService;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.authaccess.domain.model.aggregates.Role;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.RoleRepository;
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

    private final RoleRepository roleRepository;
    private final RetailCompanyRepository retailCompanyRepository;
    private final AuthorizationService authorizationService;

    public RoleController(RoleRepository roleRepository,
                          RetailCompanyRepository retailCompanyRepository,
                          AuthorizationService authorizationService) {
        this.roleRepository = roleRepository;
        this.retailCompanyRepository = retailCompanyRepository;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    @Operation(summary = "List roles for current retail company")
    public List<RoleDto> listRoles() {
        authorizationService.requireActor(UserActor.RETAIL);
        Long companyId = authorizationService.getCurrentUserCompanyId().value();
        return roleRepository.findByRetailCompany_Id(companyId).stream()
                .map(role -> new RoleDto(role.getRoleId(), role.getName()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create role for current company")
    public RoleDto createRole(@RequestBody CreateRolePayload payload) {
        authorizationService.requireActor(UserActor.RETAIL);
        Long companyId = authorizationService.getCurrentUserCompanyId().value();
        var company = retailCompanyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        var role = new Role(company, payload.name());
        var saved = roleRepository.save(role);
        return new RoleDto(saved.getRoleId(), saved.getName());
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Update role details")
    public RoleDto updateRole(@PathVariable Long roleId, @RequestBody CreateRolePayload payload) {
        authorizationService.requireActor(UserActor.RETAIL);
        Long companyId = authorizationService.getCurrentUserCompanyId().value();
        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        if (!role.getRetailCompany().getRetailCompanyId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden access");
        }
        role.rename(payload.name());
        var saved = roleRepository.save(role);
        return new RoleDto(saved.getRoleId(), saved.getName());
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete role")
    public void deleteRole(@PathVariable Long roleId) {
        authorizationService.requireActor(UserActor.RETAIL);
        Long companyId = authorizationService.getCurrentUserCompanyId().value();
        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        if (!role.getRetailCompany().getRetailCompanyId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden access");
        }
        roleRepository.delete(role);
    }

    public record RoleDto(Long roleId, String name) {}
    public record CreateRolePayload(String name) {}
}
