package com.authservice.dto.response;

import com.authservice.entity.AuthProviderEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import java.util.Set;

@Getter
@RequiredArgsConstructor
public class OAuthRegisterResponse {
    private final long userId;
    private final AuthProviderEnum authProvider;

    @Setter
    private Set<String> roleNames;
}
