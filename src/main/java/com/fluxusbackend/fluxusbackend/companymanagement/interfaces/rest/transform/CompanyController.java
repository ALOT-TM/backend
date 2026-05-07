package com.fluxusbackend.fluxusbackend.companymanagement.interfaces.rest.transform;

import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.aggregates.Company;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.commands.CreateCompanyCommand;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.queries.GetCompanyByIdQuery;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.queries.ListCompaniesQuery;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.services.CompanyCommandService;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.services.CompanyQueryService;
import com.fluxusbackend.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
@Tag(name = "Company Management", description = "Company administration")
public class CompanyController {

    private final CompanyCommandService commandService;
    private final CompanyQueryService queryService;

    public CompanyController(CompanyCommandService commandService, CompanyQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create company")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company created",
                    content = @Content(schema = @Schema(implementation = Company.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public Company create(@Valid @RequestBody CreateCompanyCommand command) {
        return commandService.handle(command);
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "Get company by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found",
                    content = @Content(schema = @Schema(implementation = Company.class))),
            @ApiResponse(responseCode = "404", description = "Company not found", content = @Content)
    })
    public Company getById(@PathVariable Long companyId) {
        return queryService.handle(new GetCompanyByIdQuery(new CompanyId(companyId)))
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
    }

    @GetMapping
    @Operation(summary = "List companies")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Companies retrieved",
                    content = @Content(schema = @Schema(implementation = Company.class)))
    })
    public List<Company> list() {
        return queryService.handle(new ListCompaniesQuery());
    }
}
