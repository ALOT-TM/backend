package com.fluxusbackend.companyretail.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetailCompanyRepository extends JpaRepository<RetailCompany, Long> {
}

