package com.fluxusbackend.fluxusbackend.identityaccessmanagement.interfaces.rest.transform;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.commands.RegisterUserCommand;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.queries.LoginUserQuery;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services.UserAuthenticationQueryService;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.services.UserCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/iam")
@Tag(name = "Identity & Access", description = "User registration and login")
public class IamController {

    private final UserCommandService userCommandService;
    private final UserAuthenticationQueryService userAuthenticationQueryService;

    public IamController(
            UserCommandService userCommandService,
            UserAuthenticationQueryService userAuthenticationQueryService
    ) {
        this.userCommandService = userCommandService;
        this.userAuthenticationQueryService = userAuthenticationQueryService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered",
                    content = @Content(schema = @Schema(implementation = UserAccount.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public UserAccount register(@Valid @RequestBody RegisterUserCommand command) {
        return userCommandService.handle(command);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = UserAccount.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    public UserAccount login(@Valid @RequestBody LoginUserQuery query) {
        return userAuthenticationQueryService.handle(query);
    }
}

