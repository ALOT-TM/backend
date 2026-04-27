package com.fluxusbackend.fluxusbackend.mermamanagement.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.aggregates.Merma;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.enums.MermaStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MermaRepository extends JpaRepository<Merma, Long> {
    List<Merma> findByStatus(MermaStatus status);
}

