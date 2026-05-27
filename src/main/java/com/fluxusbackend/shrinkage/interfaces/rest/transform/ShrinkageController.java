package com.fluxusbackend.shrinkage.interfaces.rest.transform;

import com.fluxusbackend.authaccess.application.internal.services.AuthorizationService;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.shrinkage.domain.model.aggregates.Shrinkage;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonableCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonatedCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageNotDonableCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.RegisterShrinkageCommand;
import com.fluxusbackend.shrinkage.domain.model.enums.ShrinkageStatus;
import com.fluxusbackend.shrinkage.domain.model.queries.GetShrinkageByIdQuery;
import com.fluxusbackend.shrinkage.domain.model.queries.ListShrinkagesByCompanyQuery;
import com.fluxusbackend.shrinkage.domain.model.queries.ListShrinkagesByStatusQuery;
import com.fluxusbackend.shrinkage.domain.model.valueobjects.ShrinkageId;
import com.fluxusbackend.shrinkage.domain.services.ShrinkageCommandService;
import com.fluxusbackend.shrinkage.domain.services.ShrinkageQueryService;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/shrinkages")
@Tag(name = "Shrinkage", description = "Shrinkage operations")
@SecurityRequirement(name = "bearer")
public class ShrinkageController {

    private final ShrinkageCommandService commandService;
    private final ShrinkageQueryService queryService;
    private final AuthorizationService authorizationService;

    public ShrinkageController(ShrinkageCommandService commandService, ShrinkageQueryService queryService,
                    AuthorizationService authorizationService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register shrinkage")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Shrinkage registered",
                    content = @Content(schema = @Schema(implementation = Shrinkage.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Shrinkage registerShrinkage(@Valid @RequestBody RegisterShrinkageCommand command) {
        authorizationService.requireActor(UserActor.RETAIL);
        return commandService.handle(command);
    }

    @PatchMapping("/{shrinkageId}/donable")
    @Operation(summary = "Mark shrinkage as donable")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shrinkage marked donable",
                    content = @Content(schema = @Schema(implementation = Shrinkage.class))),
            @ApiResponse(responseCode = "404", description = "Shrinkage not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Shrinkage markDonable(@PathVariable Long shrinkageId) {
        authorizationService.requireActor(UserActor.RETAIL);
        return commandService.handle(new MarkShrinkageDonableCommand(new ShrinkageId(shrinkageId)));
    }

    @PatchMapping("/{shrinkageId}/not-donable")
    @Operation(summary = "Mark shrinkage as not donable")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shrinkage marked not donable",
                    content = @Content(schema = @Schema(implementation = Shrinkage.class))),
            @ApiResponse(responseCode = "404", description = "Shrinkage not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Shrinkage markNotDonable(@PathVariable Long shrinkageId) {
        authorizationService.requireActor(UserActor.RETAIL);
        return commandService.handle(new MarkShrinkageNotDonableCommand(new ShrinkageId(shrinkageId)));
    }

    @PatchMapping("/{shrinkageId}/donated")
    @Operation(summary = "Mark shrinkage as donated")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shrinkage marked donated",
                    content = @Content(schema = @Schema(implementation = Shrinkage.class))),
            @ApiResponse(responseCode = "404", description = "Shrinkage not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Shrinkage markDonated(@PathVariable Long shrinkageId) {
        authorizationService.requireActor(UserActor.RETAIL);
        return commandService.handle(new MarkShrinkageDonatedCommand(new ShrinkageId(shrinkageId)));
    }

    @GetMapping("/{shrinkageId}")
    @Operation(summary = "Get shrinkage by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shrinkage found",
                    content = @Content(schema = @Schema(implementation = Shrinkage.class))),
            @ApiResponse(responseCode = "404", description = "Shrinkage not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Shrinkage getById(@PathVariable Long shrinkageId) {
                authorizationService.requireActor(UserActor.RETAIL, UserActor.BENEFICIARY);
        var shrinkage = queryService.handle(new GetShrinkageByIdQuery(new ShrinkageId(shrinkageId)))
                .orElseThrow(() -> new IllegalArgumentException("Shrinkage not found"));
                if (authorizationService.getCurrentUserActor() == UserActor.BENEFICIARY) {
                        if (shrinkage.getStatus() != ShrinkageStatus.DONABLE) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Shrinkage not available");
                        }
                        return shrinkage;
                }
                var currentCompany = authorizationService.getCurrentUserCompanyId();
                var shrinkageCompany = shrinkage.getCompanyId().map(CompanyId::value).orElse(null);
                if (shrinkageCompany == null || !shrinkageCompany.equals(currentCompany.value())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Company mismatch");
                }
        return shrinkage;
    }

    @GetMapping
    @Operation(summary = "List shrinkages by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shrinkages retrieved",
                    content = @Content(schema = @Schema(implementation = Shrinkage.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
        public List<Shrinkage> listByStatus(
                        @RequestParam ShrinkageStatus status,
                        @RequestParam(required = false) Long companyId
        ) {
                authorizationService.requireActor(UserActor.RETAIL, UserActor.BENEFICIARY);
                if (authorizationService.getCurrentUserActor() == UserActor.BENEFICIARY) {
                        if (status != ShrinkageStatus.DONABLE) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Status not available");
                        }
                        return queryService.handle(new ListShrinkagesByStatusQuery(ShrinkageStatus.DONABLE));
                }
                var currentCompany = authorizationService.getCurrentUserCompanyId();
                if (companyId != null && !companyId.equals(currentCompany.value())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Company mismatch");
                }
                var resolvedCompanyId = companyId == null ? currentCompany : new CompanyId(companyId);
                return queryService.handle(new ListShrinkagesByCompanyQuery(resolvedCompanyId)).stream()
                                .filter(shrinkage -> shrinkage.getStatus() == status)
                                .toList();
    }

    @GetMapping("/company")
    @Operation(summary = "List shrinkages for retail company")
    public List<Shrinkage> listByCompany() {
        authorizationService.requireActor(UserActor.RETAIL);
        var companyId = authorizationService.getCurrentUserCompanyId();
        return queryService.handle(new ListShrinkagesByCompanyQuery(companyId));
    }

    @GetMapping("/donable")
    @Operation(summary = "List donable shrinkages for beneficiaries and retailers")
    public List<Shrinkage> listDonableForViewer(@RequestParam(required = false) Long companyId) {
        return listDonableForViewerInternal(companyId);
    }

    private List<Shrinkage> listDonableForViewerInternal(Long companyId) {
        var actor = authorizationService.getCurrentUserActor();
        if (actor == UserActor.BENEFICIARY) {
            return queryService.handle(new ListShrinkagesByStatusQuery(ShrinkageStatus.DONABLE));
        }

                var currentCompany = authorizationService.getCurrentUserCompanyId();
                if (companyId != null && !companyId.equals(currentCompany.value())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Company mismatch");
                }
                var resolvedCompanyId = companyId == null ? currentCompany : new CompanyId(companyId);
        return queryService.handle(new ListShrinkagesByCompanyQuery(resolvedCompanyId)).stream()
                .filter(shrinkage -> shrinkage.getStatus() == ShrinkageStatus.DONABLE)
                .toList();
    }
}

