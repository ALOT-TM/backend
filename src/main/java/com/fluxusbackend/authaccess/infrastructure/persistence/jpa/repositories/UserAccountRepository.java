package com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.authaccess.domain.model.aggregates.UserAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("select u from UserAccount u join u.retailUser ru where ru.role.id = :roleId")
    List<UserAccount> findAllRetailUsersByRoleId(@Param("roleId") Long roleId);
}


