package com.userservice.projection;

import com.userservice.entity.AuthProviderEnum;

public interface ResetPasswordProjection {
    long getId();
    AuthProviderEnum getAuthProvider();
}
