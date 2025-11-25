package com.authservice.services.user;

import com.authservice.dto.request.ChangeEmailRequest;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.EmailChangeRequest;
import com.authservice.entity.User;
import com.authservice.services.CodeService;
import com.authservice.services.EmailChangeRequestService;
import com.authservice.services.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailChangeService {
    private final MailService mailService;
    private final EmailChangeRequestService emailChangeRequestService;
    private final UserService userService;
    private final CodeService codeService;
    private final PasswordService passwordService;

    @Transactional
    public void createRequest(ChangeEmailRequest dto, long userId) {
        User user = userService.findById(userId);

        boolean isPasswordEqual = passwordService.comparePassword(user.getPassword(), dto.password());

        if (!isPasswordEqual) {
            throw new IllegalArgumentException("Password doesn't match");
        }

        emailChangeRequestService.createOrUpdateEmailChangeRequest(user, dto.newEmail());
        Code code = codeService.create(user, CodeScopeEnum.EMAIL_CHANGE);
        mailService.sendEmailChange(user.getEmail(), code.getCode());
    }

    @Transactional
    public void changeEmail(String providedCode) {
        Code code = codeService.validate(providedCode, CodeScopeEnum.EMAIL_CHANGE);
        User user = code.getUser();
        EmailChangeRequest changeRequest = emailChangeRequestService.getByUserId(user.getId());

        user.setEmail(changeRequest.getNewEmail());

        emailChangeRequestService.deleteEmailChangeRequest(changeRequest);
    }
}
