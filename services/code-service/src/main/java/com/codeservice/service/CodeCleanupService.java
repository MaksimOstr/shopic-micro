package com.codeservice.service;

import com.codeservice.repository.CodeRepository;
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
