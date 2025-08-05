package com.authservice.services.user;

import com.authservice.dto.request.ChangePasswordRequest;
import com.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordEncoder passwordEncoder;
    private final UserQueryService userQueryService;

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean comparePassword(String oldPassword, String newPassword) {
        return passwordEncoder.matches(newPassword, oldPassword);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest dto, long userId) {
        User user = userQueryService.findById(userId);
        boolean isEqual = comparePassword(user.getPassword(), dto.oldPassword());

        if(!isEqual) {
            throw new IllegalArgumentException("Password does not match");
        }

        String encodedNewPassword = encode(dto.newPassword());
        user.setPassword(encodedNewPassword);
    }

}
