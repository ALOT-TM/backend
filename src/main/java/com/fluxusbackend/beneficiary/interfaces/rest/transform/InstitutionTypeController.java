package com.fluxusbackend.beneficiary.interfaces.rest.transform;

import com.fluxusbackend.beneficiary.domain.model.aggregates.InstitutionType;
import com.fluxusbackend.beneficiary.domain.model.commands.RegisterInstitutionTypeCommand;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.InstitutionTypeRepository;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/institution-types")
@Tag(name = "Institution Types", description = "Beneficiary institution types")
public class InstitutionTypeController {

    private final InstitutionTypeRepository repository;

    public InstitutionTypeController(InstitutionTypeRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register institution type")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Institution type registered",
                    content = @Content(schema = @Schema(implementation = InstitutionType.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public InstitutionType register(@Valid @RequestBody RegisterInstitutionTypeCommand command) {
        var institutionType = new InstitutionType(command.name());
        return repository.save(institutionType);
    }

    @GetMapping
    @Operation(summary = "List institution types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Institution types retrieved",
                    content = @Content(schema = @Schema(implementation = InstitutionType.class)))
    })
    public List<InstitutionType> list() {
        return repository.findAll();
    }
}

