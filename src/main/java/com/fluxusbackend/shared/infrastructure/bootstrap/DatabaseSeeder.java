package com.fluxusbackend.shared.infrastructure.bootstrap;

import com.fluxusbackend.beneficiary.domain.model.aggregates.InstitutionType;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.InstitutionTypeRepository;
import com.fluxusbackend.location.domain.model.aggregates.Country;
import com.fluxusbackend.location.infrastructure.persistence.jpa.repositories.CountryRepository;
import com.fluxusbackend.shrinkage.domain.model.aggregates.Category;
import com.fluxusbackend.shrinkage.domain.model.aggregates.ShrinkageReason;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.CategoryRepository;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageReasonRepository;
import com.fluxusbackend.subscription.domain.model.aggregates.Plan;
import com.fluxusbackend.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final CountryRepository countryRepository;
    private final InstitutionTypeRepository institutionTypeRepository;
    private final CategoryRepository categoryRepository;
    private final ShrinkageReasonRepository shrinkageReasonRepository;
    private final PlanRepository planRepository;

    public DatabaseSeeder(
            CountryRepository countryRepository,
            InstitutionTypeRepository institutionTypeRepository,
            CategoryRepository categoryRepository,
            ShrinkageReasonRepository shrinkageReasonRepository,
            PlanRepository planRepository
    ) {
        this.countryRepository = countryRepository;
        this.institutionTypeRepository = institutionTypeRepository;
        this.categoryRepository = categoryRepository;
        this.shrinkageReasonRepository = shrinkageReasonRepository;
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedCountries();
        seedInstitutionTypes();
        seedCategories();
        seedShrinkageReasons();
        seedPlans();
    }

    private void seedCountries() {
        if (countryRepository.count() == 0) {
            List<String> countries = List.of("Perú", "Colombia", "Chile", "México", "Ecuador", "Argentina");
            for (String name : countries) {
                countryRepository.save(new Country(name));
            }
            System.out.println("Seeded countries catalog.");
        }
    }

    private void seedInstitutionTypes() {
        if (institutionTypeRepository.count() == 0) {
            List<String> types = List.of(
                    "Banco de Alimentos",
                    "Comedor Popular",
                    "Albergue de Menores",
                    "Asociación Vecinal",
                    "Colegio Público"
            );
            for (String name : types) {
                institutionTypeRepository.save(new InstitutionType(name));
            }
            System.out.println("Seeded institution types catalog.");
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            List<String> categories = List.of(
                    "Frutas y Verduras",
                    "Lácteos",
                    "Carnes y Pescados",
                    "Panadería",
                    "Abarrotes"
            );
            for (String name : categories) {
                categoryRepository.save(new Category(name));
            }
            System.out.println("Seeded categories catalog.");
        }
    }

    private void seedShrinkageReasons() {
        if (shrinkageReasonRepository.count() == 0) {
            List<String> reasons = List.of(
                    "Fecha de vencimiento próxima",
                    "Empaque dañado",
                    "Sobre stock/Excedente de demanda",
                    "Otro"
            );
            for (String name : reasons) {
                shrinkageReasonRepository.save(new ShrinkageReason(name));
            }
            System.out.println("Seeded shrinkage reasons catalog.");
        }
    }

    private void seedPlans() {
        if (planRepository.count() == 0) {
            planRepository.save(new Plan((short) 1, "Básico", new java.math.BigDecimal("49.00"), true, 3, 1000));
            planRepository.save(new Plan((short) 2, "Profesional", new java.math.BigDecimal("149.00"), true, 15, 10000));
            planRepository.save(new Plan((short) 3, "Enterprise", new java.math.BigDecimal("999.00"), true, 999, 99999));
            System.out.println("Seeded subscription plans catalog.");
        }
    }
}
