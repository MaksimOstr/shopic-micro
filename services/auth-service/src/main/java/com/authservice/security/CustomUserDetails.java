package com.authservice.security;

import com.authservice.entity.Role;
import com.authservice.entity.User;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private final String email;
    private String password;
    private final boolean isNonBlocked;
    private final boolean isVerified;
    private final Collection<? extends GrantedAuthority> authorities;
    @Getter
    private final long userId;
    @Getter
    private final User user;

    public CustomUserDetails(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.isNonBlocked = user.getIsNonBlocked();
        this.isVerified = user.getIsVerified();
        this.userId = user.getId();
        this.authorities = mapToAuthorities(user.getRoles());
        this.user = user;
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
        return isNonBlocked;
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    private Collection<? extends GrantedAuthority> mapToAuthorities(Set<Role> roles) {
            return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }
}
