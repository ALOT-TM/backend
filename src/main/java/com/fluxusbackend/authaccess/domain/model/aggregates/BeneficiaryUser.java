package com.fluxusbackend.authaccess.domain.model.aggregates;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitution;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "beneficiary_user")
public class BeneficiaryUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beneficiary_user_id", nullable = false, updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficiary_institution_id", nullable = false)
    private BeneficiaryInstitution beneficiaryInstitution;

    protected BeneficiaryUser() {
    }

    public BeneficiaryUser(UserAccount userAccount, BeneficiaryInstitution beneficiaryInstitution) {
        this.userAccount = Objects.requireNonNull(userAccount, "User account is required");
        this.beneficiaryInstitution = Objects.requireNonNull(beneficiaryInstitution, "Beneficiary institution is required");
    }

    public Long getId() {
        return id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public BeneficiaryInstitution getBeneficiaryInstitution() {
        return beneficiaryInstitution;
    }
}
