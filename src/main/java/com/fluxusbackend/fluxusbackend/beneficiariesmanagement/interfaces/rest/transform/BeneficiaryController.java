package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.interfaces.rest.transform;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.aggregates.Beneficiary;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.ActivateBeneficiaryCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.DeactivateBeneficiaryCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.RegisterBeneficiaryCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.commands.UpdateBeneficiaryInfoCommand;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.enums.BeneficiaryStatus;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.queries.GetBeneficiaryByIdQuery;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.queries.ListBeneficiariesByStatusQuery;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.valueobjects.BeneficiaryId;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.services.BeneficiaryCommandService;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.services.BeneficiaryQueryService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.application.internal.services.AuthorizationService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beneficiaries")
@Tag(name = "Beneficiaries Management", description = "Beneficiary administration")
public class BeneficiaryController {

    private final BeneficiaryCommandService commandService;
    private final BeneficiaryQueryService queryService;
    private final AuthorizationService authorizationService;

    public BeneficiaryController(BeneficiaryCommandService commandService, BeneficiaryQueryService queryService,
            AuthorizationService authorizationService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register beneficiary")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Beneficiary registered",
                    content = @Content(schema = @Schema(implementation = Beneficiary.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Beneficiary register(@Valid @RequestBody RegisterBeneficiaryCommand command) {
        authorizationService.requireRole(UserRole.MANAGER);
        return commandService.handle(command);
    }

    @PutMapping("/{beneficiaryId}")
    @Operation(summary = "Update beneficiary info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiary updated",
                    content = @Content(schema = @Schema(implementation = Beneficiary.class))),
            @ApiResponse(responseCode = "404", description = "Beneficiary not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Beneficiary update(
            @PathVariable Long beneficiaryId,
            @Valid @RequestBody UpdateBeneficiaryInfoCommand command) {
        authorizationService.requireRole(UserRole.MANAGER);
        var normalized = new UpdateBeneficiaryInfoCommand(
                new BeneficiaryId(beneficiaryId),
                command.name(),
                command.type(),
                command.address(),
                command.acceptedProducts()
        );
        return commandService.handle(normalized);
    }

    @PatchMapping("/{beneficiaryId}/activate")
    @Operation(summary = "Activate beneficiary")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiary activated",
                    content = @Content(schema = @Schema(implementation = Beneficiary.class))),
            @ApiResponse(responseCode = "404", description = "Beneficiary not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Beneficiary activate(@PathVariable Long beneficiaryId) {
        authorizationService.requireRole(UserRole.MANAGER);
        return commandService.handle(new ActivateBeneficiaryCommand(new BeneficiaryId(beneficiaryId)));
    }

    @PatchMapping("/{beneficiaryId}/deactivate")
    @Operation(summary = "Deactivate beneficiary")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiary deactivated",
                    content = @Content(schema = @Schema(implementation = Beneficiary.class))),
            @ApiResponse(responseCode = "404", description = "Beneficiary not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Beneficiary deactivate(@PathVariable Long beneficiaryId) {
        authorizationService.requireRole(UserRole.MANAGER);
        return commandService.handle(new DeactivateBeneficiaryCommand(new BeneficiaryId(beneficiaryId)));
    }

    @GetMapping("/{beneficiaryId}")
    @Operation(summary = "Get beneficiary by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiary found",
                    content = @Content(schema = @Schema(implementation = Beneficiary.class))),
            @ApiResponse(responseCode = "404", description = "Beneficiary not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Beneficiary getById(@PathVariable Long beneficiaryId) {
        authorizationService.requireRole(UserRole.MANAGER);
        return queryService.handle(new GetBeneficiaryByIdQuery(new BeneficiaryId(beneficiaryId)))
                .orElseThrow(() -> new IllegalArgumentException("Beneficiary not found"));
    }

    @GetMapping
    @Operation(summary = "List beneficiaries by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiaries retrieved",
                    content = @Content(schema = @Schema(implementation = Beneficiary.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<Beneficiary> listByStatus(@RequestParam BeneficiaryStatus status) {
        authorizationService.requireRole(UserRole.MANAGER);
        return queryService.handle(new ListBeneficiariesByStatusQuery(status));
    }
}
