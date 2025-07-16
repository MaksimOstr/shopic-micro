package com.authservice.mapper;

import com.authservice.dto.UserDto;
import com.authservice.dto.UserSummaryDto;
import com.authservice.dto.response.CreateLocalUserResponse;
import com.authservice.dto.response.CreateOAuthUserResponse;
import com.authservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "userId", source = "user.id")
    CreateLocalUserResponse toCreateLocalUserResponse(User user);

    @Mapping(target = "firstName", source = "user.profile.firstName")
    @Mapping(target = "lastName", source = "user.profile.lastName")
    UserDto toUserDetailsDto(User user);

    @Mapping(target = "roleNames", source = "roles")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "provider", source = "authProvider")
    CreateOAuthUserResponse toCreateOAuthUserResponse(User user);

    @Mapping(target = "firstName", source = "user.profile.firstName")
    @Mapping(target = "lastName", source = "user.profile.lastName")
    UserSummaryDto toUserSummaryDto(User user);


}
