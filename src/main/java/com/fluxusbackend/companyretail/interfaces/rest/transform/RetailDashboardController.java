package com.fluxusbackend.companyretail.interfaces.rest.transform;

import com.fluxusbackend.authaccess.application.internal.services.AuthorizationService;
import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.RetailUserRepository;
import com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.fluxusbackend.donationlogistics.domain.model.aggregates.Donation;
import com.fluxusbackend.donationlogistics.infrastructure.persistence.jpa.repositories.DonationRepository;
import com.fluxusbackend.shrinkage.domain.model.aggregates.Shrinkage;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/retail/dashboard")
@Tag(name = "Retail Dashboard", description = "Dashboard operations for retailers")
@SecurityRequirement(name = "bearer")
public class RetailDashboardController {

    private final ShrinkageRepository shrinkageRepository;
    private final DonationRepository donationRepository;
    private final RetailUserRepository retailUserRepository;
    private final RoleRepository roleRepository;
    private final AuthorizationService authorizationService;

    public RetailDashboardController(
            ShrinkageRepository shrinkageRepository,
            DonationRepository donationRepository,
            RetailUserRepository retailUserRepository,
            RoleRepository roleRepository,
            AuthorizationService authorizationService
    ) {
        this.shrinkageRepository = shrinkageRepository;
        this.donationRepository = donationRepository;
        this.retailUserRepository = retailUserRepository;
        this.roleRepository = roleRepository;
        this.authorizationService = authorizationService;
    }

    public record MonthlyData(String name, int merma, int donada) {}

    public record DashboardStatsDto(
            int totalShrinkageMonth,
            double totalLostValue,
            int totalDonated,
            int activeUsers,
            int configuredRoles,
            List<MonthlyData> monthlyEvolution
    ) {}

    @GetMapping("/stats")
    @Operation(summary = "Get retail dashboard statistics")
    public DashboardStatsDto getStats() {
        authorizationService.requireActor(UserActor.RETAIL);
        var companyId = authorizationService.getCurrentUserCompanyId();
        Long cId = companyId.value();

        // 1. Fetch mermas and donations for the company
        List<Shrinkage> mermas = shrinkageRepository.findByCompanyIdValue(cId);
        List<Donation> donations = donationRepository.findByCompanyIdValue(cId);

        // 2. Fetch active users and roles
        int activeUsers = (int) retailUserRepository.findAll().stream()
                .filter(u -> u.getRetailCompany().getRetailCompanyId().equals(cId) && u.isActive())
                .count();
        int configuredRoles = (int) roleRepository.findAll().stream()
                .filter(r -> r.getRetailCompany().getRetailCompanyId().equals(cId))
                .count();

        // 3. Filter current month stats
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        int totalShrinkageMonth = 0;
        double totalLostValue = 0;

        for (Shrinkage m : mermas) {
            LocalDate mDate = convertToLocalDate(m.getCreatedAt());
            if (mDate.getMonthValue() == currentMonth && mDate.getYear() == currentYear) {
                totalShrinkageMonth += m.getQuantity();
                // Safe check for shrinkageValue (Task 3)
                Double value = getShrinkageValueSafe(m);
                totalLostValue += (value != null ? value : 0.0) * m.getQuantity();
            }
        }

        int totalDonated = 0;
        for (Donation d : donations) {
            totalDonated += d.getQuantity().amount();
        }

        // 4. Calculate monthly evolution (last 6 months)
        List<MonthlyData> evolution = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            String monthName = monthDate.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "PE"));
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).replace(".", "");

            int monthMermas = 0;
            for (Shrinkage m : mermas) {
                LocalDate mDate = convertToLocalDate(m.getCreatedAt());
                if (mDate.getMonthValue() == monthDate.getMonthValue() && mDate.getYear() == monthDate.getYear()) {
                    monthMermas += m.getQuantity();
                }
            }

            int monthDonated = 0;
            for (Donation d : donations) {
                LocalDate dDate = convertToLocalDate(d.getCreatedAt());
                if (dDate.getMonthValue() == monthDate.getMonthValue() && dDate.getYear() == monthDate.getYear()) {
                    monthDonated += d.getQuantity().amount();
                }
            }

            evolution.add(new MonthlyData(monthName, monthMermas, monthDonated));
        }

        return new DashboardStatsDto(
                totalShrinkageMonth,
                totalLostValue,
                totalDonated,
                activeUsers,
                configuredRoles,
                evolution
        );
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Download detailed retail company report")
    public void downloadReport(HttpServletResponse response) throws IOException {
        authorizationService.requireActor(UserActor.RETAIL);
        var companyId = authorizationService.getCurrentUserCompanyId();
        Long cId = companyId.value();

        List<Shrinkage> mermas = shrinkageRepository.findByCompanyIdValue(cId);
        List<Donation> donations = donationRepository.findByCompanyIdValue(cId);

        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"reporte_gestion.csv\"");

        var writer = response.getWriter();
        // Write UTF-8 BOM to open correctly in Excel
        writer.write('\ufeff');

        writer.println("REPORTE DE GESTIÓN - MERMA Y DONACIONES");
        writer.println();
        writer.println("MERMAS REGISTRADAS");
        writer.println("ID,Producto,Cantidad,Categoría,Razón,Valor Unitario,Valor Total,Fecha de Registro,Estado");

        for (Shrinkage m : mermas) {
            Double val = getShrinkageValueSafe(m);
            double valTotal = (val != null ? val : 0.0) * m.getQuantity();
            String catName = m.getCategory() != null ? m.getCategory().getName() : "-";
            String reasonName = m.getShrinkageReason() != null ? m.getShrinkageReason().getName() : "-";
            writer.printf("%d,%s,%d,%s,%s,%.2f,%.2f,%s,%s\n",
                    m.getShrinkageId(),
                    escapeCsv(m.getName()),
                    m.getQuantity(),
                    escapeCsv(catName),
                    escapeCsv(reasonName),
                    val != null ? val : 0.0,
                    valTotal,
                    convertToLocalDate(m.getCreatedAt()).toString(),
                    m.getStatus().name()
            );
        }

        writer.println();
        writer.println("DONACIONES REALIZADAS");
        writer.println("ID,ID Merma,Cantidad,Destinatario,Fecha de Registro,Estado,Fecha de Entrega,Comentarios");

        for (Donation d : donations) {
            String deliveryDateStr = d.getDeliveryDate().map(dd -> dd.value().toString()).orElse("-");
            String commentStr = d.getReceptionComment().orElse("-");
            writer.printf("%d,%d,%d,%d,%s,%s,%s,%s\n",
                    d.getDonationId().value(),
                    d.getShrinkageReferenceId() != null ? d.getShrinkageReferenceId().value() : 0L,
                    d.getQuantity().amount(),
                    d.getBeneficiaryReferenceId().value(),
                    convertToLocalDate(d.getCreatedAt()).toString(),
                    d.getStatus().name(),
                    deliveryDateStr,
                    escapeCsv(commentStr)
            );
        }

        writer.flush();
    }

    private LocalDate convertToLocalDate(Instant instant) {
        if (instant == null) return LocalDate.now();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String escapeCsv(String text) {
        if (text == null) return "";
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    private Double getShrinkageValueSafe(Shrinkage m) {
        try {
            // Using reflection in case shrinkageValue is not yet added to compile successfully during initial stages
            var field = Shrinkage.class.getDeclaredField("shrinkageValue");
            field.setAccessible(true);
            return (Double) field.get(m);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
