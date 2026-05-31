package com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.authaccess.domain.model.aggregates.RetailUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetailUserRepository extends JpaRepository<RetailUser, Long> {
    java.util.List<RetailUser> findByRetailCompany_Id(Long retailCompanyId);
    java.util.Optional<RetailUser> findByUserAccount_Id(Long userAccountId);
}
