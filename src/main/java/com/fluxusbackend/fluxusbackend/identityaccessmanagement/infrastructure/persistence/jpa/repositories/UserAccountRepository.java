package com.fluxusbackend.fluxusbackend.identityaccessmanagement.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailValue(String value);
}

