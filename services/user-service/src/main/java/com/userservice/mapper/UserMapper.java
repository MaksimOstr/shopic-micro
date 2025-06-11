package com.userservice.mapper;

import com.shopic.grpc.userservice.*;
import com.userservice.dto.CreateProfileDto;
import com.userservice.dto.request.CreateLocalUserRequest;
import com.userservice.dto.request.CreateOAuthUserRequest;
import com.userservice.dto.response.CreateOAuthUserResponse;
import com.userservice.dto.response.CreateLocalUserResponse;
import com.userservice.entity.Profile;
import com.userservice.entity.User;
import org.mapstruct.*;

import static com.userservice.utils.UserUtils.userRolesToRoleNames;


@Mapper(componentModel = "spring", uses = {InstantMapper.class})
public interface UserMapper {
    CreateProfileDto toCreateProfileDto(ProfileRequest profile);

    @Mapping(target = "userId", source = "user.id")
    CreateLocalUserResponse toCreateUserResponseDto(User user, Profile profile);

    CreateLocalUserRequest toCreateLocalUserRequest(CreateLocalUserGrpcRequest request);

    CreateLocalUserGrpcResponse toCreateUserGrpcResponse(CreateLocalUserResponse responseDto);

    CreateOAuthUserRequest toCreateOAuthUserRequest(CreateOAuthUserGrpcRequest request);

    CreateOAuthUserGrpcResponse toCreateOAuthUserGrpcResponse(CreateOAuthUserResponse response);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "isVerified", source = "isVerified")
    @Mapping(target = "roleNamesList", ignore = true)
    @Mapping(target = "provider", source = "authProvider")
    @Mapping(target = "password", ignore = true)
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
