package com.fluxusbackend.authaccess.domain.model.aggregates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluxusbackend.authaccess.domain.model.valueobjects.EmailAddress;
import com.fluxusbackend.authaccess.domain.model.valueobjects.PasswordHash;
import com.fluxusbackend.authaccess.domain.model.valueobjects.UserId;
import com.fluxusbackend.shared.domain.model.aggregates.AuditableAggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "user_account")
@AttributeOverride(name = "id", column = @Column(name = "user_account_id", nullable = false, updatable = false))
public class UserAccount extends AuditableAggregateRoot {

    @Embedded
    private EmailAddress email;

    @Embedded
    private PasswordHash passwordHash;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RetailUser retailUser;

    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BeneficiaryUser beneficiaryUser;

    protected UserAccount() {
    }

    public UserAccount(EmailAddress email, PasswordHash passwordHash, String username) {
        this.email = Objects.requireNonNull(email, "Email is required");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash is required");
        this.username = Objects.requireNonNull(username, "Username is required");
    }

    public UserId getUserId() {
        return new UserId(getId());
    }

    public EmailAddress getEmail() {
        return email;
    }

    @JsonIgnore
    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public Optional<RetailUser> getRetailUser() {
        return Optional.ofNullable(retailUser);
    }

    public Optional<BeneficiaryUser> getBeneficiaryUser() {
        return Optional.ofNullable(beneficiaryUser);
    }

    public void attachRetailUser(RetailUser retailUser) {
        this.retailUser = Objects.requireNonNull(retailUser, "Retail user is required");
    }

    public void attachBeneficiaryUser(BeneficiaryUser beneficiaryUser) {
        this.beneficiaryUser = Objects.requireNonNull(beneficiaryUser, "Beneficiary user is required");
    }
}


