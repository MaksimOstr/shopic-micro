package com.authservice.services.code;

import com.authservice.repositories.CodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeCleanupService {
    private final CodeRepository codeRepository;


    @Scheduled(fixedDelay = 900 * 1000)
    public void clearExpiredCodes() {
        log.info("Clearing expired codes");
        codeRepository.deleteExpiredCodes();
    }
}
