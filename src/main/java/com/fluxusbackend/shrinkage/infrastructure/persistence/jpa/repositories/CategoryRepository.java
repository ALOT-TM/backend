package com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.shrinkage.domain.model.aggregates.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByNameIgnoreCase(String name);
}
