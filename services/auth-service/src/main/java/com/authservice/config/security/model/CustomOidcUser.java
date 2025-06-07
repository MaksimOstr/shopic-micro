package com.authservice.config.security.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomOidcUser extends DefaultOidcUser {
    private final long id;
    private final List<String> roleNames;

    public CustomOidcUser(OidcIdToken idToken, Collection<? extends GrantedAuthority> authorities, long userId, List<String> roleNames) {
        super(authorities, idToken, "email");

        this.id = userId;
        this.roleNames = roleNames;
    }
}
