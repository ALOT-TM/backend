package com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.authaccess.domain.model.aggregates.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    java.util.Optional<Role> findFirstByRetailCompany_Id(Long retailCompanyId);
}
