package com.fluxusbackend.fluxusbackend.donationsmanagement.interfaces.rest.transform;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.Donation;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.ConfirmDonationReceptionCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CreateDonationCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.MarkDonationDeliveredCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.enums.DonationStatus;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.GetDonationByIdQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationsByBeneficiaryQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationsByStatusQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.DonationId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationCommandService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donations")
@Tag(name = "Donations Management", description = "Donation operations")
public class DonationController {

    private final DonationCommandService commandService;
    private final DonationQueryService queryService;

    public DonationController(DonationCommandService commandService, DonationQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create donation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Donation created",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "404", description = "Merma or beneficiary not found", content = @Content)
    })
    public Donation createDonation(@Valid @RequestBody CreateDonationCommand command) {
        return commandService.handle(command);
    }

    @PatchMapping("/{donationId}/delivered")
    @Operation(summary = "Mark donation as delivered")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donation marked delivered",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "404", description = "Donation not found", content = @Content)
    })
    public Donation markDelivered(@PathVariable Long donationId, @Valid @RequestBody MarkDonationDeliveredCommand command) {
        var normalized = new MarkDonationDeliveredCommand(new DonationId(donationId), command.deliveryDate());
        return commandService.handle(normalized);
    }

    @PatchMapping("/{donationId}/confirm")
    @Operation(summary = "Confirm donation reception")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donation confirmed",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "404", description = "Donation not found", content = @Content)
    })
    public Donation confirmReception(
            @PathVariable Long donationId,
            @Valid @RequestBody ConfirmDonationReceptionCommand command
    ) {
        var normalized = new ConfirmDonationReceptionCommand(
                new DonationId(donationId),
                command.receptionDate(),
                command.comment()
        );
        return commandService.handle(normalized);
    }

    @GetMapping("/{donationId}")
    @Operation(summary = "Get donation by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donation found",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "404", description = "Donation not found", content = @Content)
    })
    public Donation getById(@PathVariable Long donationId) {
        return queryService.handle(new GetDonationByIdQuery(new DonationId(donationId)))
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
    }

    @GetMapping
    @Operation(summary = "List donations by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donations retrieved",
                    content = @Content(schema = @Schema(implementation = Donation.class)))
    })
    public List<Donation> listByStatus(@RequestParam DonationStatus status) {
        return queryService.handle(new ListDonationsByStatusQuery(status));
    }

    @GetMapping("/by-beneficiary/{beneficiaryId}")
    @Operation(summary = "List donations by beneficiary")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donations retrieved",
                    content = @Content(schema = @Schema(implementation = Donation.class)))
    })
    public List<Donation> listByBeneficiary(@PathVariable Long beneficiaryId) {
        return queryService.handle(new ListDonationsByBeneficiaryQuery(new BeneficiaryReferenceId(beneficiaryId)));
    }
}

