package com.authservice.services;

import com.authservice.dto.ForgotPasswordRequest;
import com.authservice.dto.ResetPasswordRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final MailService mailService;
    private final UserService userService;
    private final CodeService codeService;

    public void requestResetPassword(ForgotPasswordRequest dto) {
        userService.findOptionalByEmail(dto.email())
                .ifPresent(user -> {
                    if (user.getAuthProvider() == AuthProviderEnum.LOCAL) {
                        Code code = codeService.create(user, CodeScopeEnum.RESET_PASSWORD);
                        mailService.sendForgotPasswordChange(user.getEmail(), code.getCode());
                        return;
                    }
                    log.info("User is not present, skipping...");
                });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest dto) {
        Code code = codeService.validate(dto.code(), CodeScopeEnum.RESET_PASSWORD);
        userService.changeUserPassword(code.getUser(), dto.newPassword());
    }
}
