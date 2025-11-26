package com.authservice.mapper;

import com.authservice.dto.UserDto;
import com.authservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
