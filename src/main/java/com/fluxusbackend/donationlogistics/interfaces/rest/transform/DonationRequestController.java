package com.fluxusbackend.donationlogistics.interfaces.rest.transform;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.donationlogistics.domain.model.commands.AcceptDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CancelDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CreateDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.RejectDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.queries.GetDonationRequestByIdQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationRequestsByBeneficiaryQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationRequestsByShrinkageQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationRequestsByCompanyQuery;
import com.fluxusbackend.donationlogistics.domain.model.queries.ListDonationRequestsByProductNameQuery;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationRequestId;
import com.fluxusbackend.donationlogistics.domain.services.DonationRequestCommandService;
import com.fluxusbackend.donationlogistics.domain.services.DonationRequestQueryService;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.authaccess.application.internal.services.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Donation Requests", description = "Beneficiary donation requests")
@SecurityRequirement(name = "bearer")
public class DonationRequestController {

    private final DonationRequestCommandService commandService;
    private final DonationRequestQueryService queryService;
    private final AuthorizationService authorizationService;

    public DonationRequestController(
            DonationRequestCommandService commandService,
            DonationRequestQueryService queryService,
            AuthorizationService authorizationService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create donation request (beneficiary claims donable shrinkage)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Donation request created",
                    content = @Content(schema = @Schema(implementation = DonationRequest.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public DonationRequest create(@Valid @RequestBody CreateDonationRequestPayload payload) {
        authorizationService.requireActor(UserActor.BENEFICIARY);
        var beneficiaryId = authorizationService.getCurrentBeneficiaryInstitutionId();
        if (beneficiaryId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Beneficiary id missing");
        }
        var normalized = new CreateDonationRequestCommand(
                payload.mermaId(),
                beneficiaryId,
                payload.notes()
        );
        return commandService.handle(normalized);
    }

    public record CreateDonationRequestPayload(@NotNull Long mermaId, String notes) {
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "Get donation request by id")
    public DonationRequest getById(@PathVariable Long requestId) {
        return queryService.handle(new GetDonationRequestByIdQuery(new DonationRequestId(requestId)))
                .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));
    }

    @GetMapping
    @Operation(summary = "List donation requests by beneficiary")
    public List<DonationRequest> listByBeneficiary(@RequestParam Long beneficiaryId) {
        authorizationService.requireActor(UserActor.BENEFICIARY);
        return queryService.handle(new ListDonationRequestsByBeneficiaryQuery(new BeneficiaryReferenceId(beneficiaryId)));
    }

    @GetMapping("/shrinkage/{shrinkageId}")
    @Operation(summary = "List donation requests for a shrinkage (manager only)")
    public List<DonationRequest> listByShrinkage(@PathVariable Long shrinkageId) {
        authorizationService.requireActor(UserActor.RETAIL);
        return queryService.handle(new ListDonationRequestsByShrinkageQuery(new com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId(shrinkageId)));
    }

    @PatchMapping("/{requestId}/accept")
    @Operation(summary = "Accept donation request (manager only)")
    public DonationRequest accept(@PathVariable Long requestId) {
        authorizationService.requireActor(UserActor.RETAIL);
        return commandService.handle(new AcceptDonationRequestCommand(new DonationRequestId(requestId)));
    }

    @PatchMapping("/{requestId}/reject")
    @Operation(summary = "Reject donation request (manager only)")
    public DonationRequest reject(@PathVariable Long requestId) {
        authorizationService.requireActor(UserActor.RETAIL);
        return commandService.handle(new RejectDonationRequestCommand(new DonationRequestId(requestId)));
    }

    @PatchMapping("/{requestId}/cancel")
    @Operation(summary = "Cancel donation request (beneficiary only)")
    public DonationRequest cancel(@PathVariable Long requestId) {
        authorizationService.requireActor(UserActor.BENEFICIARY);
        return commandService.handle(new CancelDonationRequestCommand(new DonationRequestId(requestId)));
    }

    @GetMapping("/company")
    @Operation(summary = "List donation requests for manager's company")
    public List<DonationRequest> listByCompany() {
        authorizationService.requireActor(UserActor.RETAIL);
        var companyId = authorizationService.getCurrentUserCompanyId();
        return queryService.handle(new ListDonationRequestsByCompanyQuery(companyId));
    }

    @GetMapping("/product/{productName}")
    @Operation(summary = "List donation requests for shrinkage by product name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donation requests retrieved",
                    content = @Content(schema = @Schema(implementation = DonationRequest.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<DonationRequest> listByProductName(@PathVariable String productName) {
        authorizationService.requireActor(UserActor.RETAIL);
        return queryService.handle(new ListDonationRequestsByProductNameQuery(productName));
    }
}

