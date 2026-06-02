package com.fluxusbackend.subscription.application.internal.commandservices;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
import com.fluxusbackend.companyretail.infrastructure.persistence.jpa.repositories.RetailCompanyRepository;
import com.fluxusbackend.subscription.domain.model.aggregates.Plan;
import com.fluxusbackend.subscription.domain.model.aggregates.Subscription;
import com.fluxusbackend.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.fluxusbackend.subscription.domain.model.enums.SubscriptionStatus;
import com.fluxusbackend.subscription.domain.services.SubscriptionCommandService;
import com.fluxusbackend.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import com.fluxusbackend.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final RetailCompanyRepository retailCompanyRepository;
    private final PlanRepository planRepository;

    public SubscriptionCommandServiceImpl(
            SubscriptionRepository subscriptionRepository,
            RetailCompanyRepository retailCompanyRepository,
            PlanRepository planRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.retailCompanyRepository = retailCompanyRepository;
        this.planRepository = planRepository;
    }

    @Override
    @Transactional
    public Subscription handle(CreateSubscriptionCommand command) {
        RetailCompany company = retailCompanyRepository.findById(command.retailCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Retail company not found"));
        Plan plan = planRepository.findById(command.planId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(30, ChronoUnit.DAYS); // Standard 30 days subscription

        var subscription = new Subscription(
                company,
                plan,
                SubscriptionStatus.ACTIVE,
                startDate,
                endDate
        );

        return subscriptionRepository.save(subscription);
    }
}
