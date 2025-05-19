package com.userservice.mapper;

import com.shopic.grpc.userservice.CreateLocalUserRequest;
import com.shopic.grpc.userservice.CreateUserResponse;
import com.shopic.grpc.userservice.ProfileRequest;
import com.shopic.grpc.userservice.UserForAuthResponse;
import com.userservice.dto.CreateProfileDto;
import com.userservice.dto.request.CreateLocalUserRequestDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.Profile;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {InstantMapper.class})
public interface UserMapper {
    CreateProfileDto toCreateProfileDto(ProfileRequest profile);

    @Mapping(target = "userId", source = "user.id")
    CreateUserResponseDto toCreateUserResponseDto(User user, Profile profile);

    CreateLocalUserRequestDto toCreateLocalUserRequestDto(CreateLocalUserRequest request);

    CreateUserResponse toGrpcCreateUserResponse(CreateUserResponseDto responseDto);

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
}
