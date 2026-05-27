package com.fluxusbackend.donationlogistics.application.internal.eventlisteners;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.ShrinkageLog;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId;
import com.fluxusbackend.donationlogistics.infrastructure.persistence.jpa.repositories.ShrinkageLogRepository;
import com.fluxusbackend.shrinkage.domain.model.events.ShrinkageStatusChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ShrinkageLogListener {

    private final ShrinkageLogRepository repository;

    public ShrinkageLogListener(ShrinkageLogRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void onShrinkageStatusChanged(ShrinkageStatusChangedEvent event) {
        var log = new ShrinkageLog(
            new ShrinkageReferenceId(event.shrinkageId().value()),
            event.oldStatus(),
            event.newStatus()
        );
        repository.save(log);
    }
}
