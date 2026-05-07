package com.fluxusbackend.fluxusbackend.companymanagement.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.aggregates.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
