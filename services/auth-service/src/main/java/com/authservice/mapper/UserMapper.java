package com.authservice.mapper;

import com.authservice.dto.UserDetailsDto;
import com.authservice.dto.UserSummaryDto;
import com.authservice.dto.response.CreateOAuthUserResponse;
import com.authservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "roleNames", source = "roles")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "provider", source = "authProvider")
    CreateOAuthUserResponse toCreateOAuthUserResponse(User user);

    UserSummaryDto toUserSummaryDto(User user);

    UserDetailsDto toUserDetailsDto(User user);
}
