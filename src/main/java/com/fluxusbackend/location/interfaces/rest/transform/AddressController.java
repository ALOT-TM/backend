package com.fluxusbackend.location.interfaces.rest.transform;

import com.fluxusbackend.location.domain.model.aggregates.Address;
import com.fluxusbackend.location.domain.model.commands.CreateAddressCommand;
import com.fluxusbackend.location.infrastructure.persistence.jpa.repositories.AddressRepository;
import com.fluxusbackend.location.infrastructure.persistence.jpa.repositories.CountryRepository;
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
@RequestMapping("/api/addresses")
@Tag(name = "Addresses", description = "Address administration")
public class AddressController {

    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;

    public AddressController(AddressRepository addressRepository, CountryRepository countryRepository) {
        this.addressRepository = addressRepository;
        this.countryRepository = countryRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create address")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Address created",
                    content = @Content(schema = @Schema(implementation = Address.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public Address create(@Valid @RequestBody CreateAddressCommand command) {
        var country = countryRepository.findById(command.countryId())
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
        var address = new Address(
                command.street1(),
                command.street2(),
                command.city(),
                command.stateProvince(),
                command.postalCode(),
                country
        );
        return addressRepository.save(address);
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get address by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address found",
                    content = @Content(schema = @Schema(implementation = Address.class))),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public Address getById(@PathVariable Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
    }

    @GetMapping
    @Operation(summary = "List addresses")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Addresses retrieved",
                    content = @Content(schema = @Schema(implementation = Address.class)))
    })
    public List<Address> list() {
        return addressRepository.findAll();
    }
}

