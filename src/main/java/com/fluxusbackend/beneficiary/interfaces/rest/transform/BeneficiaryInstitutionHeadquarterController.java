package com.fluxusbackend.beneficiary.interfaces.rest.transform;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitutionHeadquarter;
import com.fluxusbackend.beneficiary.domain.model.commands.CreateBeneficiaryInstitutionHeadquarterCommand;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.BeneficiaryInstitutionHeadquarterRepository;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.BeneficiaryInstitutionRepository;
import com.fluxusbackend.location.infrastructure.persistence.jpa.repositories.AddressRepository;
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
@RequestMapping("/api/beneficiary-institution-headquarters")
@Tag(name = "Beneficiary Institution Headquarters", description = "Beneficiary institution headquarters administration")
public class BeneficiaryInstitutionHeadquarterController {

    private final BeneficiaryInstitutionHeadquarterRepository headquarterRepository;
    private final BeneficiaryInstitutionRepository beneficiaryInstitutionRepository;
    private final AddressRepository addressRepository;

    public BeneficiaryInstitutionHeadquarterController(
            BeneficiaryInstitutionHeadquarterRepository headquarterRepository,
            BeneficiaryInstitutionRepository beneficiaryInstitutionRepository,
            AddressRepository addressRepository
    ) {
        this.headquarterRepository = headquarterRepository;
        this.beneficiaryInstitutionRepository = beneficiaryInstitutionRepository;
        this.addressRepository = addressRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create beneficiary institution headquarter")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Headquarter created",
                    content = @Content(schema = @Schema(implementation = BeneficiaryInstitutionHeadquarter.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public BeneficiaryInstitutionHeadquarter create(@Valid @RequestBody CreateBeneficiaryInstitutionHeadquarterCommand command) {
        var beneficiary = beneficiaryInstitutionRepository.findById(command.beneficiaryInstitutionId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficiary institution not found"));
        var address = addressRepository.findById(command.addressId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
        var headquarter = new BeneficiaryInstitutionHeadquarter(beneficiary, command.description(), address);
        return headquarterRepository.save(headquarter);
    }

    @GetMapping("/{headquarterId}")
    @Operation(summary = "Get beneficiary institution headquarter by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Headquarter found",
                    content = @Content(schema = @Schema(implementation = BeneficiaryInstitutionHeadquarter.class))),
            @ApiResponse(responseCode = "404", description = "Headquarter not found", content = @Content)
    })
    public BeneficiaryInstitutionHeadquarter getById(@PathVariable Long headquarterId) {
        return headquarterRepository.findById(headquarterId)
                .orElseThrow(() -> new IllegalArgumentException("Headquarter not found"));
    }

    @GetMapping
    @Operation(summary = "List beneficiary institution headquarters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Headquarters retrieved",
                    content = @Content(schema = @Schema(implementation = BeneficiaryInstitutionHeadquarter.class)))
    })
    public List<BeneficiaryInstitutionHeadquarter> list() {
        return headquarterRepository.findAll();
    }
}

