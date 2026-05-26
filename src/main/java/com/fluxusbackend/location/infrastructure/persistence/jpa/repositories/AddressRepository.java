package com.fluxusbackend.location.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.location.domain.model.aggregates.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
