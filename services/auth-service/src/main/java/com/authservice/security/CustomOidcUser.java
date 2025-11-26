package com.authservice.security;

import com.authservice.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomOidcUser extends DefaultOidcUser {
    private final User user;
    private final List<String> roleNames;

    public CustomOidcUser(OidcIdToken idToken, Collection<? extends GrantedAuthority> authorities, User user, List<String> roleNames) {
        super(authorities, idToken, "email");

        this.user = user;
        this.roleNames = roleNames;
    }
}
