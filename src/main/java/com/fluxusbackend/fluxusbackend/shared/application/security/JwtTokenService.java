package com.fluxusbackend.fluxusbackend.shared.application.security;

import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.enums.UserRole;
import com.fluxusbackend.fluxusbackend.identityaccessmanagement.domain.model.aggregates.UserAccount;
import com.fluxusbackend.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtTokenService(
            @Value("${authorization.jwt.secret:fluxusbackenddevsecretkeyfluxusbackenddevsecret}") String secret,
            @Value("${authorization.jwt.expiration-milliseconds:86400000}") long expirationMillis
    ) {
        this.signingKey = createSigningKey(secret);
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(UserAccount user) {
        return generateToken(
                user.getUserId().value(),
                user.getEmail().value(),
                user.getRole(),
                user.getCompanyId().orElse(null)
        );
    }

    public String generateToken(Long userId, String email, UserRole role, CompanyId companyId) {
        var now = new Date();
        var expirationDate = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role.name())
                .claim("companyId", companyId == null ? null : companyId.value())
            .setIssuedAt(now)
            .setExpiration(expirationDate)
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Optional<AuthInfo> parseToken(String token) {
        try {
            var claims = parseClaims(stripBearerPrefix(token));
            var userId = claims.getSubject() == null ? null : Long.parseLong(claims.getSubject());
            var email = claims.get("email", String.class);
            var roleText = claims.get("role", String.class);
            var companyClaim = claims.get("companyId");
            var role = roleText == null ? null : UserRole.valueOf(roleText);
            var companyId = companyClaim instanceof Number number ? new CompanyId(number.longValue()) : null;
            return Optional.of(new AuthInfo(userId, email, role, companyId));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public String getBearerTokenFrom(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }
        return authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
    }

    public String getBearerTokenFrom(jakarta.servlet.http.HttpServletRequest request) {
        return getBearerTokenFrom(request.getHeader("Authorization"));
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String stripBearerPrefix(String token) {
        if (token == null) {
            return null;
        }
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }

    private SecretKey createSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public record AuthInfo(Long userId, String email, UserRole role, CompanyId companyId) {
    }
}
