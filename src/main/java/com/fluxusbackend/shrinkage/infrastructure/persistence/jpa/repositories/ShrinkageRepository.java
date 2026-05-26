package com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.shrinkage.domain.model.aggregates.Shrinkage;
import com.fluxusbackend.shrinkage.domain.model.enums.ShrinkageStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ShrinkageRepository extends JpaRepository<Shrinkage, Long> {
    List<Shrinkage> findByStatus(ShrinkageStatus status);

    @Query("select s from Shrinkage s where s.companyId.value = :companyId")
    List<Shrinkage> findByCompanyIdValue(@Param("companyId") Long companyId);
}


