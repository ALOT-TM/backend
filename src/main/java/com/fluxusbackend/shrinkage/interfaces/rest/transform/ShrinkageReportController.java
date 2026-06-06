package com.fluxusbackend.shrinkage.interfaces.rest.transform;

import com.fluxusbackend.authaccess.application.internal.services.AuthorizationService;
import com.fluxusbackend.shrinkage.domain.model.aggregates.Shrinkage;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Shrinkage Reports", description = "Advanced reporting and summaries for shrinkages")
@SecurityRequirement(name = "bearer")
public class ShrinkageReportController {

    private final ShrinkageRepository shrinkageRepository;
    private final AuthorizationService authorizationService;

    public ShrinkageReportController(ShrinkageRepository shrinkageRepository, AuthorizationService authorizationService) {
        this.shrinkageRepository = shrinkageRepository;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/shrinkage-summary")
    @PreAuthorize("hasRole('RETAIL') and principal.companyId() != null and principal.companyId().value() == #companyId")
    @Operation(summary = "Get shrinkage summary for reports")
    public ShrinkageSummaryDto getShrinkageSummary(@RequestParam Long companyId) {
        List<Shrinkage> mermas = shrinkageRepository.findByCompanyIdValue(companyId);

        int totalQuantity = mermas.stream()
                .mapToInt(Shrinkage::getQuantity)
                .sum();

        double totalValue = mermas.stream()
                .mapToDouble(m -> (m.getShrinkageValue() != null ? m.getShrinkageValue() : 0.0) * m.getQuantity())
                .sum();

        int totalItems = mermas.size();

        return new ShrinkageSummaryDto(companyId, totalQuantity, totalValue, totalItems);
    }

    public record ShrinkageSummaryDto(Long companyId, int totalQuantity, double totalValue, int totalItems) {}
}
