package com.fluxusbackend.beneficiary.interfaces.rest.transform;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitution;
import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitutionHeadquarter;
import com.fluxusbackend.beneficiary.domain.model.commands.RegisterBeneficiaryCommand;
import com.fluxusbackend.beneficiary.domain.model.commands.UpdateBeneficiaryInfoCommand;
import com.fluxusbackend.beneficiary.domain.model.queries.GetBeneficiaryByIdQuery;
import com.fluxusbackend.beneficiary.domain.model.queries.ListBeneficiaryInstitutionsQuery;
import com.fluxusbackend.beneficiary.domain.model.valueobjects.BeneficiaryId;
import com.fluxusbackend.beneficiary.domain.services.BeneficiaryCommandService;
import com.fluxusbackend.beneficiary.domain.services.BeneficiaryQueryService;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.BeneficiaryInstitutionHeadquarterRepository;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.BeneficiaryInstitutionRepository;
import com.fluxusbackend.location.infrastructure.persistence.jpa.repositories.AddressRepository;
import com.fluxusbackend.location.infrastructure.persistence.jpa.repositories.CountryRepository;
import com.fluxusbackend.authaccess.application.internal.services.AuthorizationService;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/beneficiary-institutions")
@Tag(name = "Beneficiary Institutions", description = "Beneficiary institution administration")
public class BeneficiaryInstitutionController {

    private final BeneficiaryCommandService commandService;
    private final BeneficiaryQueryService queryService;
    private final AuthorizationService authorizationService;
    private final BeneficiaryInstitutionRepository beneficiaryInstitutionRepository;
    private final BeneficiaryInstitutionHeadquarterRepository headquarterRepository;
    private final CountryRepository countryRepository;
    private final AddressRepository addressRepository;

    public BeneficiaryInstitutionController(
            BeneficiaryCommandService commandService,
            BeneficiaryQueryService queryService,
            AuthorizationService authorizationService,
            BeneficiaryInstitutionRepository beneficiaryInstitutionRepository,
            BeneficiaryInstitutionHeadquarterRepository headquarterRepository,
            CountryRepository countryRepository,
            AddressRepository addressRepository
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.authorizationService = authorizationService;
        this.beneficiaryInstitutionRepository = beneficiaryInstitutionRepository;
        this.headquarterRepository = headquarterRepository;
        this.countryRepository = countryRepository;
        this.addressRepository = addressRepository;
    }

    @PutMapping("/me")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer")
    @Operation(summary = "Update logged-in beneficiary institution details")
    @org.springframework.transaction.annotation.Transactional
    public BeneficiaryInstitution updateMe(@Valid @RequestBody UpdateBeneficiaryMePayload payload) {
        authorizationService.requireActor(UserActor.BENEFICIARY);
        var beneficiaryId = authorizationService.getCurrentBeneficiaryInstitutionId();
        if (beneficiaryId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Beneficiary institution ID missing");
        }

        var beneficiary = beneficiaryInstitutionRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiary institution not found"));

        beneficiary.updateInfo(beneficiary.getInstitutionType(), payload.name());
        var savedBeneficiary = beneficiaryInstitutionRepository.save(beneficiary);

        var hqOpt = headquarterRepository.findAll().stream()
                .filter(h -> h.getBeneficiaryInstitution().getBeneficiaryInstitutionId().equals(beneficiaryId))
                .findFirst();

        if (hqOpt.isPresent()) {
            var hq = hqOpt.get();
            hq.updateDescription(payload.notes());
            hq.getAddress().updateStreet1(payload.address());
            headquarterRepository.save(hq);
        } else {
            var country = countryRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No country available in database"));
            var address = new com.fluxusbackend.location.domain.model.aggregates.Address(
                    payload.address(), "", "Lima", "Lima", "15001", country
            );
            var savedAddress = addressRepository.save(address);
            var newHq = new BeneficiaryInstitutionHeadquarter(beneficiary, payload.notes(), savedAddress);
            headquarterRepository.save(newHq);
        }

        return savedBeneficiary;
    }

    public record UpdateBeneficiaryMePayload(
            @jakarta.validation.constraints.NotBlank String name,
            @jakarta.validation.constraints.NotBlank String address,
            String notes
    ) {}

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
