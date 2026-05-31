package com.fluxusbackend.shrinkage.interfaces.rest.transform;

import com.fluxusbackend.authaccess.application.internal.services.AuthorizationService;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.shrinkage.domain.model.aggregates.ShrinkageReason;
import com.fluxusbackend.shrinkage.domain.model.commands.CreateShrinkageReasonCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.UpdateShrinkageReasonCommand;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageReasonRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shrinkages/reasons")
@Tag(name = "Shrinkage", description = "Shrinkage operations")
@SecurityRequirement(name = "bearer")
public class ShrinkageReasonController {

    private final ShrinkageReasonRepository shrinkageReasonRepository;
    private final AuthorizationService authorizationService;

    public ShrinkageReasonController(
            ShrinkageReasonRepository shrinkageReasonRepository,
            AuthorizationService authorizationService
    ) {
        this.shrinkageReasonRepository = shrinkageReasonRepository;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create shrinkage reason")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Shrinkage reason created",
                    content = @Content(schema = @Schema(implementation = ShrinkageReason.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ShrinkageReason create(@Valid @RequestBody CreateShrinkageReasonCommand command) {
        authorizationService.requireActor(UserActor.RETAIL);
        var reason = new ShrinkageReason(command.name());
        return shrinkageReasonRepository.save(reason);
    }

    @PatchMapping("/{shrinkageReasonId}")
    @Operation(summary = "Patch shrinkage reason")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shrinkage reason updated",
                    content = @Content(schema = @Schema(implementation = ShrinkageReason.class))),
            @ApiResponse(responseCode = "404", description = "Shrinkage reason not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ShrinkageReason update(
            @PathVariable Long shrinkageReasonId,
            @Valid @RequestBody UpdateShrinkageReasonCommand command
    ) {
        authorizationService.requireActor(UserActor.RETAIL);
        var normalized = new UpdateShrinkageReasonCommand(shrinkageReasonId, command.name());
        var reason = shrinkageReasonRepository.findById(normalized.shrinkageReasonId())
                .orElseThrow(() -> new IllegalArgumentException("Shrinkage reason not found"));
        reason.updateName(normalized.name());
        return shrinkageReasonRepository.save(reason);
    }

        @GetMapping
        @Operation(summary = "List shrinkage reasons")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Shrinkage reasons retrieved",
                                        content = @Content(schema = @Schema(implementation = ShrinkageReason.class))),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
        })
        public List<ShrinkageReason> list() {
                authorizationService.requireActor(UserActor.RETAIL, UserActor.BENEFICIARY);
                return shrinkageReasonRepository.findAll();
        }

            @GetMapping("/{shrinkageReasonId}")
            @Operation(summary = "Get shrinkage reason by id")
            @ApiResponses({
                    @ApiResponse(responseCode = "200", description = "Shrinkage reason found",
                            content = @Content(schema = @Schema(implementation = ShrinkageReason.class))),
                    @ApiResponse(responseCode = "404", description = "Shrinkage reason not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
            })
            public ShrinkageReason getById(@PathVariable Long shrinkageReasonId) {
                authorizationService.requireActor(UserActor.RETAIL, UserActor.BENEFICIARY);
                return shrinkageReasonRepository.findById(shrinkageReasonId)
                        .orElseThrow(() -> new IllegalArgumentException("Shrinkage reason not found"));
            }
}

