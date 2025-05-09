package com.userservice.mapper;

import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.Profile;
import com.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "phoneNumber", source = "profile.phoneNumber")
    @Mapping(target = "birthDate", source = "profile.birthDate")
    @Mapping(target = "avatar", source = "profile.avatar")
    @Mapping(target = "createdAt", source = "user.createdAt")
    CreateUserResponseDto toCreateUserResponse(User user, Profile profile);
}
