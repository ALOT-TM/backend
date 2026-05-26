package com.fluxusbackend.location.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.location.domain.model.aggregates.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
