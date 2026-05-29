package com.fluxusbackend.location.interfaces.rest.transform;

import com.fluxusbackend.location.domain.model.aggregates.Country;
import com.fluxusbackend.location.domain.model.commands.CreateCountryCommand;
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
@RequestMapping("/api/countries")
@Tag(name = "Countries", description = "Country administration")
public class CountryController {

    private final CountryRepository countryRepository;

    public CountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create country")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Country created",
                    content = @Content(schema = @Schema(implementation = Country.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public Country create(@Valid @RequestBody CreateCountryCommand command) {
        var country = new Country(command.name());
        return countryRepository.save(country);
    }

    @GetMapping("/{countryId}")
    @Operation(summary = "Get country by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Country found",
                    content = @Content(schema = @Schema(implementation = Country.class))),
            @ApiResponse(responseCode = "404", description = "Country not found", content = @Content)
    })
    public Country getById(@PathVariable Long countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
    }

    @GetMapping
    @Operation(summary = "List countries")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Countries retrieved",
                    content = @Content(schema = @Schema(implementation = Country.class)))
    })
    public List<Country> list() {
        return countryRepository.findAll();
    }
}

