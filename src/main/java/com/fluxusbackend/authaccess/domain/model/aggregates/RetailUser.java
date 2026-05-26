package com.fluxusbackend.authaccess.domain.model.aggregates;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
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
@Table(name = "retail_user")
public class RetailUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "retail_user_id", nullable = false, updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "retail_company_id", nullable = false)
    private RetailCompany retailCompany;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "status", nullable = false)
    private boolean status;

    protected RetailUser() {
    }

    public RetailUser(UserAccount userAccount, RetailCompany retailCompany, Role role, boolean status) {
        this.userAccount = Objects.requireNonNull(userAccount, "User account is required");
        this.retailCompany = Objects.requireNonNull(retailCompany, "Retail company is required");
        this.role = Objects.requireNonNull(role, "Role is required");
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public RetailCompany getRetailCompany() {
        return retailCompany;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return status;
    }

    public void activate() {
        status = true;
    }

    public void deactivate() {
        status = false;
    }
}
