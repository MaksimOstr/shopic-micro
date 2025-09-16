package com.authservice.services;

import com.authservice.entity.EmailChangeRequest;
import com.authservice.entity.User;
import com.authservice.exceptions.NotFoundException;
import com.authservice.repositories.EmailChangeRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailChangeRequestService {
    private final EmailChangeRequestRepository emailChangeRequestRepository;

    public EmailChangeRequest getByUserId(long userId) {
        return emailChangeRequestRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NotFoundException("No email change request found"));
    }

    public void createOrUpdateEmailChangeRequest(User user, String newEmail) {
        emailChangeRequestRepository.findByUser_Id(user.getId())
                .ifPresentOrElse(existing -> {
                    existing.setNewEmail(newEmail);
                }, () -> {
                    EmailChangeRequest newEmailChangeRequest = EmailChangeRequest.builder()
                            .newEmail(newEmail)
                            .user(user)
                            .build();
                    emailChangeRequestRepository.save(newEmailChangeRequest);
                });
    }

    public void deleteEmailChangeRequest(EmailChangeRequest emailChangeRequest) {
        emailChangeRequestRepository.delete(emailChangeRequest);
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
