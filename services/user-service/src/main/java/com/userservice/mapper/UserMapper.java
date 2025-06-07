package com.userservice.mapper;

import com.shopic.grpc.userservice.*;
import com.userservice.dto.CreateProfileDto;
import com.userservice.dto.request.CreateLocalUserRequestDto;
import com.userservice.dto.request.CreateOAuthUserRequestDto;
import com.userservice.dto.response.CreateOAuthUserResponseDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.Profile;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import org.mapstruct.*;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {InstantMapper.class})
public interface UserMapper {
    CreateProfileDto toCreateProfileDto(ProfileRequest profile);

    @Mapping(target = "userId", source = "user.id")
    CreateUserResponseDto toCreateUserResponseDto(User user, Profile profile);

    CreateLocalUserRequestDto toCreateLocalUserRequestDto(CreateLocalUserRequest request);

    CreateLocalUserResponse toGrpcCreateUserResponse(CreateUserResponseDto responseDto);

    CreateOAuthUserRequestDto toCreateOAuthUserRequestDto(CreateOAuthUserRequest request);

    CreateOAuthUserResponse toCreateOAuthUserResponseDto(CreateOAuthUserResponseDto response);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "isVerified", source = "verified")
    @Mapping(target = "roleNamesList", ignore = true)
    UserForAuthResponse toAuthResponse(User user);


    @AfterMapping
    default void afterMapping(@MappingTarget UserForAuthResponse.Builder builder, User user) {
        builder.addAllRoleNames(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
        );
    }

    @AfterMapping
    default void afterMappingOAuthUser(@MappingTarget CreateOAuthUserResponse.Builder builder, CreateOAuthUserResponseDto responseDto) {
        builder.addAllRoleNames(responseDto.roleNames());
    }
}
