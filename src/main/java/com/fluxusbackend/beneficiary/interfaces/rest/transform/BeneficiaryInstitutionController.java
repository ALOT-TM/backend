package com.fluxusbackend.beneficiary.interfaces.rest.transform;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitution;
import com.fluxusbackend.beneficiary.domain.model.commands.RegisterBeneficiaryCommand;
import com.fluxusbackend.beneficiary.domain.model.commands.UpdateBeneficiaryInfoCommand;
import com.fluxusbackend.beneficiary.domain.model.queries.GetBeneficiaryByIdQuery;
import com.fluxusbackend.beneficiary.domain.model.queries.ListBeneficiaryInstitutionsQuery;
import com.fluxusbackend.beneficiary.domain.model.valueobjects.BeneficiaryId;
import com.fluxusbackend.beneficiary.domain.services.BeneficiaryCommandService;
import com.fluxusbackend.beneficiary.domain.services.BeneficiaryQueryService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beneficiary-institutions")
@Tag(name = "Beneficiary Institutions", description = "Beneficiary institution administration")
public class BeneficiaryInstitutionController {

    private final BeneficiaryCommandService commandService;
    private final BeneficiaryQueryService queryService;

        public BeneficiaryInstitutionController(
                BeneficiaryCommandService commandService,
                BeneficiaryQueryService queryService
        ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register beneficiary institution")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Beneficiary institution registered",
                    content = @Content(schema = @Schema(implementation = BeneficiaryInstitution.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public BeneficiaryInstitution register(@Valid @RequestBody RegisterBeneficiaryCommand command) {
        return commandService.handle(command);
    }

    @PutMapping("/{beneficiaryId}")
    @Operation(summary = "Update beneficiary institution")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiary institution updated",
                    content = @Content(schema = @Schema(implementation = BeneficiaryInstitution.class))),
            @ApiResponse(responseCode = "404", description = "Beneficiary not found", content = @Content)
    })
    public BeneficiaryInstitution update(
            @PathVariable Long beneficiaryId,
            @Valid @RequestBody UpdateBeneficiaryInfoCommand command) {
        var normalized = new UpdateBeneficiaryInfoCommand(
                new BeneficiaryId(beneficiaryId),
                command.name(),
                command.institutionTypeId()
        );
        return commandService.handle(normalized);
    }

    @GetMapping("/{beneficiaryId}")
    @Operation(summary = "Get beneficiary institution by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiary institution found",
                    content = @Content(schema = @Schema(implementation = BeneficiaryInstitution.class))),
            @ApiResponse(responseCode = "404", description = "Beneficiary not found", content = @Content)
    })
    public BeneficiaryInstitution getById(@PathVariable Long beneficiaryId) {
        return queryService.handle(new GetBeneficiaryByIdQuery(new BeneficiaryId(beneficiaryId)))
                .orElseThrow(() -> new IllegalArgumentException("Beneficiary not found"));
    }

    @GetMapping
    @Operation(summary = "List beneficiary institutions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Beneficiary institutions retrieved",
                    content = @Content(schema = @Schema(implementation = BeneficiaryInstitution.class)))
    })
    public List<BeneficiaryInstitution> listAll() {
        return queryService.handle(new ListBeneficiaryInstitutionsQuery());
    }
}
