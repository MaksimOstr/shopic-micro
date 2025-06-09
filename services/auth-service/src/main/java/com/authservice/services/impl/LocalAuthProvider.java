package com.authservice.services.impl;

import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.dto.response.LocalRegisterResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface LocalAuthProvider {
    LocalRegisterResponse register(LocalRegisterRequest dto) throws JsonProcessingException;
}
