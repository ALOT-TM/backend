package com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    @EntityGraph(attributePaths = {
            "retailUser",
            "retailUser.role",
            "retailUser.retailCompany",
            "beneficiaryUser",
            "beneficiaryUser.beneficiaryInstitution"
    })
    Optional<UserAccount> findByEmailValue(String value);
}


