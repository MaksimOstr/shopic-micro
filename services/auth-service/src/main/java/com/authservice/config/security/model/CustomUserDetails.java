package com.authservice.config.security.model;

import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private final String email;
    private String password;
    private final boolean isBlocked;
    private final boolean isVerified;
    private final Collection<? extends GrantedAuthority> authorities;
    @Getter
    private final long userId;

    public CustomUserDetails(String email, String password, boolean isBlocked, boolean isVerified, long userId, List<String> roleNames) {
        this.email = email;
        this.password = password;
        this.isBlocked = isBlocked;
        this.isVerified = isVerified;
        this.userId = userId;
        this.authorities = mapToAuthorities(roleNames);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isBlocked;
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    private Collection<? extends GrantedAuthority> mapToAuthorities(List<String> roles) {
            return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }
}
