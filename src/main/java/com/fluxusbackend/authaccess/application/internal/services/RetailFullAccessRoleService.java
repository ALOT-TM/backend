package com.fluxusbackend.authaccess.application.internal.services;

import com.fluxusbackend.authaccess.domain.model.aggregates.Permission;
import com.fluxusbackend.authaccess.domain.model.aggregates.RetailUser;
import com.fluxusbackend.authaccess.domain.model.aggregates.Role;
import com.fluxusbackend.authaccess.domain.model.aggregates.RolePermission;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.PermissionRepository;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.RetailUserRepository;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.RolePermissionRepository;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RetailFullAccessRoleService {

    public static final String DEFAULT_ROLE_NAME = "RETAIL_FULL_ACCESS";

    private final RoleRepository roleRepository;
    private final RetailUserRepository retailUserRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RetailFullAccessRoleService(
            RoleRepository roleRepository,
            RetailUserRepository retailUserRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository
    ) {
        this.roleRepository = roleRepository;
        this.retailUserRepository = retailUserRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Transactional
    public Role resolveForCompany(RetailCompany company) {
        var roles = roleRepository.findByRetailCompany_Id(company.getRetailCompanyId());
        var defaultRole = roles.stream()
                .filter(role -> DEFAULT_ROLE_NAME.equals(role.getName()))
                .findFirst()
                .orElseGet(() -> roleRepository.save(new Role(company, DEFAULT_ROLE_NAME)));

        ensureRoleHasAllPermissions(defaultRole);
        removeExtraRoles(company.getRetailCompanyId(), defaultRole, roles);
        return defaultRole;
    }

    private void removeExtraRoles(Long companyId, Role defaultRole, List<Role> roles) {
        var retailUsers = retailUserRepository.findByRetailCompany_Id(companyId);
        for (var role : roles) {
            if (role.getRoleId().equals(defaultRole.getRoleId())) {
                continue;
            }
            reassignUsers(retailUsers, role, defaultRole);
            rolePermissionRepository.deleteByRole(role);
            roleRepository.delete(role);
        }
    }

    private void reassignUsers(List<RetailUser> retailUsers, Role oldRole, Role defaultRole) {
        for (var retailUser : retailUsers) {
            if (retailUser.getRole().getRoleId().equals(oldRole.getRoleId())) {
                retailUser.updateRole(defaultRole);
                retailUserRepository.save(retailUser);
            }
        }
    }

    private void ensureRoleHasAllPermissions(Role role) {
        var permissions = permissionRepository.findAll();
        if (permissions.isEmpty()) {
            var defaultPermission = new Permission((short) 1, "FULL_ACCESS");
            permissions = List.of(permissionRepository.save(defaultPermission));
        }
        for (var permission : permissions) {
            if (!rolePermissionRepository.existsByRoleAndPermission(role, permission)) {
                rolePermissionRepository.save(new RolePermission(role, permission));
            }
        }
    }
}
