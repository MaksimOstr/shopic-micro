package com.userservice.mapper;

import com.shopic.grpc.userservice.CreateUserResponse;
import com.shopic.grpc.userservice.ProfileRequest;
import com.userservice.dto.CreateProfileDto;
import com.userservice.entity.Profile;
import com.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {InstantMapper.class})
public interface UserMapper {
    CreateProfileDto toCreateProfileDto(ProfileRequest profile);

    @Mapping(target = "userId", source = "user.id")
    CreateUserResponse toGrpcCreateUserResponse(User user, Profile profile);
}
