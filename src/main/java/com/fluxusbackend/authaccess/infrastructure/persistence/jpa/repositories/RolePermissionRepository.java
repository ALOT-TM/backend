package com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.authaccess.domain.model.aggregates.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    boolean existsByRoleAndPermission(
            com.fluxusbackend.authaccess.domain.model.aggregates.Role role,
            com.fluxusbackend.authaccess.domain.model.aggregates.Permission permission
    );
}
