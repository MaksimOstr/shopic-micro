package com.userservice.mapper;

import com.shopic.grpc.userservice.*;
import com.userservice.dto.CreateProfileDto;
import com.userservice.dto.request.CreateLocalUserRequest;
import com.userservice.dto.request.CreateOAuthUserRequest;
import com.userservice.dto.response.CreateLocalUserResponse;
import com.userservice.dto.response.CreateOAuthUserResponse;
import com.userservice.entity.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static com.userservice.utils.UserUtils.userRolesToRoleNames;

@Mapper(componentModel = "spring")
public interface GrpcMapper {
    CreateProfileDto toCreateProfileDto(ProfileGrpcRequest profile);

    CreateLocalUserRequest toCreateLocalUserRequest(CreateLocalUserGrpcRequest request);

    CreateLocalUserGrpcResponse toCreateUserGrpcResponse(CreateLocalUserResponse responseDto);

    CreateOAuthUserRequest toCreateOAuthUserRequest(CreateOAuthUserGrpcRequest request);

    CreateOAuthUserGrpcResponse toCreateOAuthUserGrpcResponse(CreateOAuthUserResponse response);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "isVerified", source = "isVerified")
    @Mapping(target = "roleNamesList", ignore = true)
    @Mapping(target = "provider", source = "authProvider")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isAccountNonLocked", source = "accountNonLocked")
    UserForAuthGrpcResponse toAuthResponse(User user);

    @AfterMapping
    default void afterMapping(@MappingTarget UserForAuthGrpcResponse.Builder builder, User user) {
        builder.addAllRoleNames(userRolesToRoleNames(user.getRoles()));
    }

    @AfterMapping
    default void afterMappingOAuthUser(@MappingTarget CreateOAuthUserGrpcResponse.Builder builder, CreateOAuthUserResponse responseDto) {
        builder.addAllRoleNames(responseDto.roleNames());
    }

    @AfterMapping
    default void afterMappingUserForAuthGrpcResponse(@MappingTarget UserForAuthGrpcResponse.Builder response, User user) {
        if(user.getPassword() != null) {
            response.setPassword(user.getPassword());
        }
    }
}
