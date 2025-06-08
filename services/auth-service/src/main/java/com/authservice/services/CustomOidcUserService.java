package com.authservice.services;

import com.authservice.config.security.model.CustomOidcUser;
import com.authservice.dto.request.OAuthRegisterRequest;
import com.authservice.dto.response.OAuthRegisterResponse;
import com.authservice.enums.AuthProviderEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final AuthService authService;


    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("loadUser");
        OidcIdToken idToken = userRequest.getIdToken();
        String email = idToken.getEmail();
        String username = idToken.getClaimAsString("name");
        OAuthRegisterRequest request = new OAuthRegisterRequest(email, username, username, AuthProviderEnum.GOOGLE);

        OAuthRegisterResponse response;
        try {
            response = authService.oAuthRegister(request);
        } catch (JsonProcessingException e) {
            throw new OAuth2AuthenticationException(e.getMessage());
        }

        if(response.authProvider() != AuthProviderEnum.GOOGLE) {
            OAuth2Error oauth2Error = new OAuth2Error("This account was created by another provider");
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        return new CustomOidcUser(
                userRequest.getIdToken(),
                List.of(),
                response.userId(),
                response.roleNames()
        );
    }
}
