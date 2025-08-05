package com.authservice.projection.user;

import com.authservice.entity.AuthProviderEnum;

public interface ResetPasswordProjection {
    long getId();
    AuthProviderEnum getAuthProvider();
}
