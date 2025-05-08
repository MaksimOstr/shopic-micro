package com.userservice.services;

import com.userservice.dto.request.CreateUserRequestDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.Profile;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import com.userservice.exceptions.EntityAlreadyExists;
import com.userservice.mapper.UserMapper;
import com.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ProfileService profileService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public CreateUserResponseDto createLocalUser(CreateUserRequestDto dto) {
        if(isUserExists(dto.email())) {
            throw new EntityAlreadyExists("User with such an email already exists");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());
        Role defaultRole = roleService.getDefaultUserRole();

        User user = new User(
                dto.email(),
                encodedPassword,
                Set.of(defaultRole)
        );

        User savedUser = userRepository.save(user);
        Profile profile = profileService.createProfile(dto.profile(), savedUser);

        return userMapper.toCreateUserResponse(savedUser, profile);
    }

    private boolean isUserExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
