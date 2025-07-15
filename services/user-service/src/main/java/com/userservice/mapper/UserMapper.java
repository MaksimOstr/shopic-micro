package com.userservice.mapper;

import com.userservice.dto.UserDetailsDto;
import com.userservice.dto.UserSummaryDto;
import com.userservice.dto.response.CreateLocalUserResponse;
import com.userservice.dto.response.CreateOAuthUserResponse;
import com.userservice.entity.Profile;
import com.userservice.entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = {UserRoleMapper.class})
public interface UserMapper {

    @Mapping(target = "userId", source = "user.id")
    CreateLocalUserResponse toCreateLocalUserResponse(User user, Profile profile);

    @Mapping(target = "firstName", source = "user.profile.firstName")
    @Mapping(target = "lastName", source = "user.profile.lastName")
    UserDetailsDto toUserDetailsDto(User user);

    @Mapping(target = "roleNames", source = "roles")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "provider", source = "authProvider")
    CreateOAuthUserResponse toCreateOAuthUserResponse(User user);

    @Mapping(target = "firstName", source = "user.profile.firstName")
    @Mapping(target = "lastName", source = "user.profile.lastName")
    UserSummaryDto toUserSummaryDto(User user);


}
