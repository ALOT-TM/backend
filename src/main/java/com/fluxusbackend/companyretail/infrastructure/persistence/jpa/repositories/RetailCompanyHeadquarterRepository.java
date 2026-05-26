package com.fluxusbackend.companyretail.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompanyHeadquarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetailCompanyHeadquarterRepository extends JpaRepository<RetailCompanyHeadquarter, Long> {
}
