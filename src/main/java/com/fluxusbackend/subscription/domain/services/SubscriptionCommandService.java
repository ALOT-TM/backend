package com.fluxusbackend.subscription.domain.services;

import com.fluxusbackend.subscription.domain.model.aggregates.Subscription;
import com.fluxusbackend.subscription.domain.model.commands.CreateSubscriptionCommand;

public interface SubscriptionCommandService {
    Subscription handle(CreateSubscriptionCommand command);
}
