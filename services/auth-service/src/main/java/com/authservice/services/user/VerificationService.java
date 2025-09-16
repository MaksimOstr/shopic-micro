package com.authservice.services.user;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exceptions.EmailVerifyException;
import com.authservice.exceptions.NotFoundException;
import com.authservice.repositories.UserRepository;
import com.authservice.services.MailService;
import com.authservice.services.code.CodeCreationService;
import com.authservice.services.code.CodeValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {
    private final UserRepository userRepository;
    private final CodeCreationService codeCreationService;
    private final MailService mailService;
    private final CodeValidationService codeValidationService;
    private final UserQueryService userQueryService;

    @Transactional
    public void requestVerifyEmail(String email) {
        User user = userQueryService.findByEmail(email);

        if (user.getIsVerified()) {
            log.error("User already verified");
            throw new EmailVerifyException("Email verification request failed");
        }

        Code code = codeCreationService.getCode(user, CodeScopeEnum.EMAIL_VERIFICATION);

        mailService.sendEmailVerificationCode(user.getEmail(), code.getCode());
    }

    public void verifyUser(String providedCode) {
        Code code = codeValidationService.validate(providedCode, CodeScopeEnum.EMAIL_VERIFICATION);
        markUserVerified(code.getUser().getId());
    }


    private void markUserVerified(long userId) {
        int updated = userRepository.markUserVerified(userId);

        if (updated == 0) {
            throw new NotFoundException("User not found");
        }
    }
}
