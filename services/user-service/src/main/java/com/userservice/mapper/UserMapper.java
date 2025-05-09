package com.userservice.mapper;

import com.shopic.grpc.userservice.CreateUserResponse;
import com.shopic.grpc.userservice.ProfileRequest;
import com.userservice.dto.CreateProfileDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.Profile;
import com.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {InstantMapper.class})
public interface UserMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "phoneNumber", source = "profile.phoneNumber")
    @Mapping(target = "avatar", source = "profile.avatar")
    @Mapping(target = "createdAt", source = "user.createdAt")
    CreateUserResponseDto toCreateUserResponse(User user, Profile profile);

    CreateProfileDto toCreateProfileDto(ProfileRequest profile);


    CreateUserResponse toGrpcCreateUserResponse(User user, Profile profile);
}
