package com.fluxusbackend.subscription.interfaces.rest;

import com.fluxusbackend.subscription.domain.model.aggregates.Subscription;
import com.fluxusbackend.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.fluxusbackend.subscription.domain.services.SubscriptionCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscriptions", description = "Company subscriptions management")
public class SubscriptionController {

    private final SubscriptionCommandService subscriptionCommandService;

    public SubscriptionController(SubscriptionCommandService subscriptionCommandService) {
        this.subscriptionCommandService = subscriptionCommandService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start subscription for a company")
    public Subscription startSubscription(@RequestBody CreateSubscriptionCommand command) {
        return subscriptionCommandService.handle(command);
    }
}
