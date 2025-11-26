package com.authservice.security;

import com.authservice.dto.CreateOAuthUserRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.User;
import com.authservice.mapper.RoleMapper;
import com.authservice.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {
    private final UserService userService;
    private final RoleMapper roleMapper;


    private static final OAuth2Error error = new OAuth2Error(
            "account_linked_to_another_provider",
            "This account was registered via another provider",
            null
    );

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Loading user via oidc user service");
        OidcIdToken idToken = userRequest.getIdToken();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        AuthProviderEnum authProvider = AuthProviderEnum.fromString(provider);
        String email = idToken.getEmail();
        String firstName = idToken.getGivenName();
        String lastName = idToken.getFamilyName();

        CreateOAuthUserRequest request = new CreateOAuthUserRequest(authProvider, email, firstName, lastName);
        User user = userService.createOrGetOAuthUser(request);

        if(user.getAuthProvider().equals(AuthProviderEnum.LOCAL)) {
            log.error("User has been registered via another provider: {}", user.getAuthProvider());
            throw new OAuth2AuthenticationException(error);
        }

        return new CustomOidcUser(
                userRequest.getIdToken(),
                List.of(),
                user,
                roleMapper.mapRolesToNames(user.getRoles())
        );
    }
}
