package com.userservice.mapper;

import com.userservice.dto.response.CreateLocalUserResponse;
import com.userservice.entity.Profile;
import com.userservice.entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = {InstantMapper.class})
public interface UserMapper {

    @Mapping(target = "userId", source = "user.id")
    CreateLocalUserResponse toCreateUserResponseDto(User user, Profile profile);
}
