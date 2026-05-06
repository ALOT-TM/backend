package com.fluxusbackend.fluxusbackend.donationsmanagement.interfaces.rest.transform;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.Donation;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.ConfirmDonationReceptionCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CreateDonationCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.MarkDonationDeliveredCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.enums.DonationStatus;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.GetDonationByIdQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationsByBeneficiaryQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationsByStatusQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationStatisticsQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.interfaces.rest.dto.DonationStatisticDto;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.DonationId;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationCommandService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationQueryService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.application.internal.services.AuthorizationService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services.UserQueryService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.GetUserByIdQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.UserId;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
    private final AuthorizationService authorizationService;
        private final UserQueryService userQueryService;

    public DonationController(DonationCommandService commandService, DonationQueryService queryService,
                        AuthorizationService authorizationService, UserQueryService userQueryService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.authorizationService = authorizationService;
                this.userQueryService = userQueryService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create donation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Donation created",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "404", description = "Merma or beneficiary not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Donation createDonation(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateDonationCommand command) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        return commandService.handle(command);
    }

    @PatchMapping("/{donationId}/delivered")
    @Operation(summary = "Mark donation as delivered")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donation marked delivered",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "404", description = "Donation not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Donation markDelivered(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long donationId,
            @Valid @RequestBody MarkDonationDeliveredCommand command) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        var normalized = new MarkDonationDeliveredCommand(new DonationId(donationId), command.deliveryDate());
        return commandService.handle(normalized);
    }

    @PatchMapping("/{donationId}/confirm")
    @Operation(summary = "Confirm donation reception (BENEFICIARY)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donation confirmed",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "404", description = "Donation not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Donation confirmReception(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long donationId,
            @Valid @RequestBody ConfirmDonationReceptionCommand command) {
        authorizationService.requireRole(userId, UserRole.BENEFICIARY);
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
            @ApiResponse(responseCode = "404", description = "Donation not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Donation getById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long donationId) {
        authorizationService.requireRole(userId, UserRole.MANAGER, UserRole.BENEFICIARY);
        return queryService.handle(new GetDonationByIdQuery(new DonationId(donationId)))
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
    }

    @GetMapping
    @Operation(summary = "List donations by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donations retrieved",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<Donation> listByStatus(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam DonationStatus status) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        return queryService.handle(new ListDonationsByStatusQuery(status));
    }

    @GetMapping("/by-beneficiary/{beneficiaryId}")
    @Operation(summary = "List donations by beneficiary")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donations retrieved",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<Donation> listByBeneficiary(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long beneficiaryId) {
        authorizationService.requireRole(userId, UserRole.MANAGER, UserRole.BENEFICIARY);
        return queryService.handle(new ListDonationsByBeneficiaryQuery(new BeneficiaryReferenceId(beneficiaryId)));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get donation statistics for company (manager only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics retrieved",
                    content = @Content(schema = @Schema(implementation = DonationStatisticDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<DonationStatisticDto> getStatistics(
            @RequestHeader("X-User-Id") Long userId) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        var user = userQueryService.handle(new GetUserByIdQuery(new UserId(userId)))
                .orElseThrow(() -> new IllegalArgumentException("Unknown user"));
        var companyId = user.getCompanyId().orElseThrow(() -> new IllegalArgumentException("User has no company"));
        return queryService.handle(new ListDonationStatisticsQuery(companyId));
    }
}
