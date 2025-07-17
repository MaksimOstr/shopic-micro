package com.authservice.services;

import com.authservice.config.security.model.CustomOidcUser;
import com.authservice.dto.CreateProfileDto;
import com.authservice.dto.request.CreateOAuthUserRequest;
import com.authservice.dto.response.CreateOAuthUserResponse;
import com.authservice.services.user.OAuthUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final OAuthUserService oAuthUserService;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Loading user via oidc user service");
        OidcIdToken idToken = userRequest.getIdToken();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = idToken.getEmail();
        String username = idToken.getClaimAsString("name");

        CreateOAuthUserRequest request = new CreateOAuthUserRequest(provider, email, username, username);
        CreateOAuthUserResponse response = oAuthUserService.createOrGetOAuthUser(request);


        return new CustomOidcUser(
                userRequest.getIdToken(),
                List.of(),
                response.userId(),
                response.roleNames()
        );
    }
}
