package com.authservice.services;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        User user = userService.findByEmail(email);

        if (user.getIsVerified()) {
            log.error("User already verified");
            throw new ApiException("Email verification request failed", HttpStatus.BAD_REQUEST);
        }

        Code code = codeService.create(user, CodeScopeEnum.EMAIL_VERIFICATION);

        mailService.sendEmailVerificationCode(user.getEmail(), code.getCode());
    }

    public void verifyUser(String providedCode) {
        Code code = codeService.validate(providedCode, CodeScopeEnum.EMAIL_VERIFICATION);
        userService.updateVerificationStatus(code.getUser().getId(), true);
    }
}
