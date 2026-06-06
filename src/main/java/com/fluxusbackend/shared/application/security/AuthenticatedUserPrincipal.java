package com.fluxusbackend.shared.application.security;

import com.fluxusbackend.authaccess.domain.model.enums.UserActor;
import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record AuthenticatedUserPrincipal(
        Long userId,
        String email,
        CompanyId companyId,
        Long beneficiaryInstitutionId,
        UserActor actor,
        Long roleId,
        String roleName
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (actor != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + actor.name()));
        }
        if (roleName != null && !roleName.isBlank()) {
            var normalizedRole = roleName.trim().toUpperCase().replace(' ', '_');
            authorities.add(new SimpleGrantedAuthority(
                    normalizedRole.startsWith("ROLE_") ? normalizedRole : "ROLE_" + normalizedRole
            ));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

