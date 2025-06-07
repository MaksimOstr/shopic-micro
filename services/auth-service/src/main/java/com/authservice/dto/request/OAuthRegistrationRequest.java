package com.authservice.dto.request;

public class OAuthRegistrationRequest extends BaseAuthRequest {

    public OAuthRegistrationRequest(String email, String firstName, String lastName) {
        super(email, firstName, lastName);
    }
}
