package com.authservice.services.user;

import com.authservice.dto.request.ChangeEmailRequest;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.EmailChangeRequest;
import com.authservice.entity.User;
import com.authservice.exceptions.NotFoundException;
import com.authservice.repositories.EmailChangeRequestRepository;
import com.authservice.services.MailService;
import com.authservice.services.code.CodeCreationService;
import com.authservice.services.code.CodeValidationService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailChangeRequestService {
    private final EmailChangeRequestRepository emailChangeRequestRepository;
    private final CodeCreationService codeCreationService;
    private final MailService mailService;
    private final CodeValidationService codeValidationService;
    private final UserQueryService userQueryService;
    private final PasswordService passwordService;
    private final EntityManager entityManager;

    @Transactional
    public void createRequest(ChangeEmailRequest dto, long userId) {
        User user = userQueryService.findById(userId);

        boolean isPasswordEqual = passwordService.comparePassword(user.getPassword(), dto.password());

        if (!isPasswordEqual) {
            throw new IllegalArgumentException("Password doesn't match");
        }

        createOrUpdateEmailChangeRequest(userId, dto.newEmail());
        Code code = codeCreationService.getCode(user, CodeScopeEnum.EMAIL_CHANGE);
        mailService.sendEmailChange(user.getEmail(), code.getCode());
    }

    @Transactional
    public void changeEmail(String providedCode) {
        Code code = codeValidationService.validate(providedCode, CodeScopeEnum.EMAIL_CHANGE);
        User user = code.getUser();
        EmailChangeRequest changeRequest = emailChangeRequestRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("No email change request found"));

        user.setEmail(changeRequest.getNewEmail());
        emailChangeRequestRepository.delete(changeRequest);
    }

    private void createOrUpdateEmailChangeRequest(long userId, String newEmail) {
        emailChangeRequestRepository.findByUser_Id(userId)
                .ifPresentOrElse(existing -> {
                    existing.setNewEmail(newEmail);
                }, () -> {
                    EmailChangeRequest newEmailChangeRequest = EmailChangeRequest.builder()
                            .newEmail(newEmail)
                            .user(entityManager.getReference(User.class, userId))
                            .build();
                    emailChangeRequestRepository.save(newEmailChangeRequest);
                });
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void cleanupOldRequests() {
        Instant cutoff = Instant.now().minus(2, ChronoUnit.HOURS);
        int deletedCount = emailChangeRequestRepository.deleteAllByCreatedAtBefore(cutoff);
        if (deletedCount > 0) {
            log.info("Deleted {} expired email change requests", deletedCount);
        }
    }
}
