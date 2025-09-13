package com.authservice.services.user;

import com.authservice.dto.UserProfileResponse;
import com.authservice.dto.request.UpdateProfileRequest;
import com.authservice.entity.User;
import com.authservice.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserQueryService userQueryService;
    private final UserMapper userMapper;

    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest dto, long userId) {
        User user = userQueryService.findById(userId);

        Optional.ofNullable(dto.firstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(dto.lastName()).ifPresent(user::setLastName);

        return userMapper.toUserProfileDto(user);
    }

    public UserProfileResponse getProfile(long userId) {
        User user = userQueryService.findById(userId);

        return userMapper.toUserProfileDto(user);
    }
}
