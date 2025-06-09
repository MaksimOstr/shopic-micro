package com.userservice.utils;

import com.userservice.entity.Role;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class UserUtils {
    public static List<String> userRolesToRoleNames(Set<Role> userRoles) {
        return userRoles.stream().map(Role::getName).collect(Collectors.toList());
    }
}
