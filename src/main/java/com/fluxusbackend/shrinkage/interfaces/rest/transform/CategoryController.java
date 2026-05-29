package com.fluxusbackend.shrinkage.interfaces.rest.transform;

import com.fluxusbackend.authaccess.application.internal.services.AuthorizationService;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.shrinkage.domain.model.aggregates.Category;
import com.fluxusbackend.shrinkage.domain.model.commands.CreateCategoryCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.UpdateCategoryCommand;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shrinkages/categories")
@Tag(name = "Shrinkage", description = "Shrinkage operations")
@SecurityRequirement(name = "bearer")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final AuthorizationService authorizationService;

    public CategoryController(CategoryRepository categoryRepository, AuthorizationService authorizationService) {
        this.categoryRepository = categoryRepository;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Category create(@Valid @RequestBody CreateCategoryCommand command) {
        authorizationService.requireActor(UserActor.RETAIL);
        var category = new Category(command.name());
        return categoryRepository.save(category);
    }

    @PatchMapping("/{categoryId}")
    @Operation(summary = "Patch category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Category update(@PathVariable Long categoryId, @Valid @RequestBody UpdateCategoryCommand command) {
        authorizationService.requireActor(UserActor.RETAIL);
        var normalized = new UpdateCategoryCommand(categoryId, command.name());
        var category = categoryRepository.findById(normalized.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.updateName(normalized.name());
        return categoryRepository.save(category);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public void delete(@PathVariable Long categoryId) {
        authorizationService.requireActor(UserActor.RETAIL);
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        categoryRepository.delete(category);
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public Category getById(@PathVariable Long categoryId) {
        authorizationService.requireActor(UserActor.RETAIL, UserActor.BENEFICIARY);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    @GetMapping
    @Operation(summary = "List categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<Category> list() {
        authorizationService.requireActor(UserActor.RETAIL, UserActor.BENEFICIARY);
        return categoryRepository.findAll();
    }
}
