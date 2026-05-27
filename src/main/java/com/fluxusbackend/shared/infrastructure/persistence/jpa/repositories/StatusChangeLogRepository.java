package com.fluxusbackend.shared.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.shared.domain.model.aggregates.StatusChangeLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusChangeLogRepository extends JpaRepository<StatusChangeLog, Long> {
	List<StatusChangeLog> findByEntityType(String entityType, Sort sort);

	List<StatusChangeLog> findByEntityTypeAndEntityId(String entityType, Long entityId, Sort sort);

	List<StatusChangeLog> findByChangedByUserId(Long changedByUserId, Sort sort);

	List<StatusChangeLog> findByEntityTypeAndChangedByUserId(String entityType, Long changedByUserId, Sort sort);
}
