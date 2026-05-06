package com.fluxusbackend.fluxusbackend.mermamanagement.interfaces.rest.transform;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.application.internal.services.AuthorizationService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.aggregates.Merma;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaDonableCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaDonatedCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaNotDonableCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.RegisterMermaCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.enums.MermaStatus;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.GetMermaByIdQuery;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.ListMermasByStatusQuery;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.MermaId;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.services.MermaCommandService;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.services.MermaQueryService;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.ListMermasByCompanyQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services.UserQueryService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.GetUserByIdQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.valueobjects.UserId;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mermas")
@Tag(name = "Merma Management", description = "Merma operations for managers")
public class MermaController {

    private final MermaCommandService commandService;
    private final MermaQueryService queryService;
    private final AuthorizationService authorizationService;
        private final UserQueryService userQueryService;

        public MermaController(MermaCommandService commandService, MermaQueryService queryService,
                        AuthorizationService authorizationService, UserQueryService userQueryService) {
                this.commandService = commandService;
                this.queryService = queryService;
                this.authorizationService = authorizationService;
                this.userQueryService = userQueryService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register merma")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Merma registered",
                    content = @Content(schema = @Schema(implementation = Merma.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Merma registerMerma(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody RegisterMermaCommand command) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        return commandService.handle(command);
    }

    @PatchMapping("/{mermaId}/donable")
    @Operation(summary = "Mark merma as donable")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Merma marked donable",
                    content = @Content(schema = @Schema(implementation = Merma.class))),
            @ApiResponse(responseCode = "404", description = "Merma not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Merma markDonable(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long mermaId) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        return commandService.handle(new MarkMermaDonableCommand(new MermaId(mermaId)));
    }

    @PatchMapping("/{mermaId}/not-donable")
    @Operation(summary = "Mark merma as not donable")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Merma marked not donable",
                    content = @Content(schema = @Schema(implementation = Merma.class))),
            @ApiResponse(responseCode = "404", description = "Merma not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Merma markNotDonable(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long mermaId) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        return commandService.handle(new MarkMermaNotDonableCommand(new MermaId(mermaId)));
    }

    @PatchMapping("/{mermaId}/donated")
    @Operation(summary = "Mark merma as donated")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Merma marked donated",
                    content = @Content(schema = @Schema(implementation = Merma.class))),
            @ApiResponse(responseCode = "404", description = "Merma not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Merma markDonated(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long mermaId) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        return commandService.handle(new MarkMermaDonatedCommand(new MermaId(mermaId)));
    }

    @GetMapping("/{mermaId}")
    @Operation(summary = "Get merma by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Merma found",
                    content = @Content(schema = @Schema(implementation = Merma.class))),
            @ApiResponse(responseCode = "404", description = "Merma not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Merma getById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long mermaId) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        return queryService.handle(new GetMermaByIdQuery(new MermaId(mermaId)))
                .orElseThrow(() -> new IllegalArgumentException("Merma not found"));
    }

    @GetMapping
    @Operation(summary = "List mermas by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mermas retrieved",
                    content = @Content(schema = @Schema(implementation = Merma.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<Merma> listByStatus(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam MermaStatus status) {
        authorizationService.requireRole(userId, UserRole.MANAGER);
        return queryService.handle(new ListMermasByStatusQuery(status));
    }

        @GetMapping("/company")
        @Operation(summary = "List mermas for manager's company")
        public List<Merma> listByCompany(
                        @RequestHeader("X-User-Id") Long userId) {
                authorizationService.requireRole(userId, UserRole.MANAGER);
                var user = userQueryService.handle(new GetUserByIdQuery(new UserId(userId)))
                                .orElseThrow(() -> new IllegalArgumentException("Unknown user"));
                var companyId = user.getCompanyId().orElseThrow(() -> new IllegalArgumentException("User has no company"));
                return queryService.handle(new ListMermasByCompanyQuery(companyId));
        }

        @GetMapping("/donable")
        @Operation(summary = "List donable mermas for beneficiaries")
        public List<Merma> listDonableForBeneficiary(
                        @RequestHeader("X-User-Id") Long userId) {
                authorizationService.requireRole(userId, UserRole.BENEFICIARY);
                return queryService.handle(new ListMermasByStatusQuery(MermaStatus.DONABLE));
        }
}
