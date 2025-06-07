package com.authservice.mapper;

import com.authservice.dto.response.LocalRegisterResponse;
import com.authservice.dto.response.OAuthRegisterResponse;
import com.authservice.enums.AuthProviderEnum;
import com.shopic.grpc.userservice.CreateLocalUserGrpcResponse;
import com.shopic.grpc.userservice.CreateOAuthUserGrpcResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TimeStampMapper.class})
public interface AuthMapper {

   LocalRegisterResponse toLocalRegisterResponse(CreateLocalUserGrpcResponse response, AuthProviderEnum authProvider);

   OAuthRegisterResponse toOAuthRegisterResponse(CreateOAuthUserGrpcResponse responseDto, AuthProviderEnum authProvider);
}
