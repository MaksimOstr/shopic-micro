package com.authservice.services;

import com.authservice.entity.RefreshToken;
import com.authservice.entity.User;

public interface RefreshTokenService {
    String create(User user);

    RefreshToken validate(String refreshToken);

    void deleteRefreshToken(String token);
}
