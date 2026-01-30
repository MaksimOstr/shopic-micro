package com.authservice.services;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {
    private final MailService mailService;
    private final UserService userService;
    private final CodeService codeService;

    @Transactional
    public void requestVerifyEmail(String email) {
        userService.findOptionalByEmail(email)
                .ifPresent(user -> {
                    if (!user.getIsVerified()) {
                        Code code = codeService.create(user, CodeScopeEnum.EMAIL_VERIFICATION);
                        mailService.sendEmailVerificationCode(user.getEmail(), code.getCode());
                        return;
                    }
                    log.info("User already verified, skipping verification email");
                });

    }

    public void verifyUser(String providedCode) {
        Code code = codeService.validate(providedCode, CodeScopeEnum.EMAIL_VERIFICATION);
        userService.updateVerificationStatus(code.getUser().getId(), true);
    }
}
