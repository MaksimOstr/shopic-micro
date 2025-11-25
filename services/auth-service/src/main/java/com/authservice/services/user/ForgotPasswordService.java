package com.authservice.services.user;

import com.authservice.dto.request.ForgotPasswordRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exceptions.ResetPasswordException;
import com.authservice.services.CodeService;
import com.authservice.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final MailService mailService;
    private final PasswordService passwordService;
    private final UserService userService;
    private final CodeService codeService;

    public void requestResetPassword(ForgotPasswordRequest dto) {
        User user = userService.findByEmail(dto.email());

        if(user.getAuthProvider() != AuthProviderEnum.LOCAL) {
            throw new ResetPasswordException("User is not a local user");
        }

        Code code = codeService.create(user, CodeScopeEnum.RESET_PASSWORD);
        mailService.sendForgotPasswordChange(user.getEmail(), code.getCode());
    }

    @Transactional
    public void resetPassword(String newPassword, String providedCode) {
        Code code = codeService.validate(providedCode, CodeScopeEnum.RESET_PASSWORD);
        User user = code.getUser();

        if(passwordService.comparePassword(user.getPassword(), newPassword)) {
            throw new ResetPasswordException("Password is the same");
        }

        String encodedPassword = passwordService.encode(newPassword);
        user.setPassword(encodedPassword);
    }
}
