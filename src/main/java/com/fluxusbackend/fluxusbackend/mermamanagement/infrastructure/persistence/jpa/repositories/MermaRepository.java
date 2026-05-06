package com.fluxusbackend.fluxusbackend.mermamanagement.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.aggregates.Merma;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.enums.MermaStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface MermaRepository extends JpaRepository<Merma, Long> {
    List<Merma> findByStatus(MermaStatus status);

    @Query("select m from Merma m where m.companyId.value = :companyId")
    List<Merma> findByCompanyIdValue(@Param("companyId") Long companyId);
}

