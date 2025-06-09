package com.authservice.mapper;

import com.authservice.dto.response.LocalRegisterResponse;
import com.authservice.dto.response.OAuthRegisterResponse;
import com.authservice.enums.AuthProviderEnum;
import com.shopic.grpc.userservice.CreateLocalUserGrpcResponse;
import com.shopic.grpc.userservice.CreateOAuthUserGrpcResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashSet;

@Mapper(componentModel = "spring", uses = {TimeStampMapper.class})
public interface AuthMapper {

   LocalRegisterResponse toLocalRegisterResponse(CreateLocalUserGrpcResponse response, AuthProviderEnum authProvider);

   @Mapping(source = "authProvider", target = "authProvider")
   OAuthRegisterResponse toOAuthRegisterResponse(CreateOAuthUserGrpcResponse responseDto, AuthProviderEnum authProvider);

   @AfterMapping
   default void afterMapping(@MappingTarget OAuthRegisterResponse registerResponse, CreateOAuthUserGrpcResponse grpcResponse) {
      registerResponse.setRoleNames(new HashSet<>(grpcResponse.getRoleNamesList()));
   }
}
