package com.userservice.mapper;

import com.google.protobuf.ProtocolStringList;
import com.userservice.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

    default List<String> mapRolesToNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .toList();
    }


}
