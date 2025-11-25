package com.authservice.services;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;

public interface CodeService {
    Code create(User user, CodeScopeEnum scope);

    Code validate(String code, CodeScopeEnum scope);
}
