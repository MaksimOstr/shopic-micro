package com.userservice.services;

import com.userservice.dto.request.BanRequest;
import com.userservice.entity.Ban;
import com.userservice.entity.User;
import com.userservice.repositories.BanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BanService {
    private final BanRepository banRepository;
    private final QueryUserService queryUserService;
    private final KafkaEventProducer kafkaEventProducer;

    @Transactional
    public void banUser(BanRequest dto, long bannerId) {
        User user = queryUserService.findById(dto.userId());

        if(!user.isAccountNonLocked()) {
            throw new IllegalStateException("User account is already locked");
        }

        if(Objects.equals(user.getId(), bannerId)) {
            throw new IllegalStateException("You can't ban yourself");
        }

        Ban ban = Ban.builder()
                .banTo(dto.banTo())
                .user(user)
                .reason(dto.reason())
                .build();

        banRepository.save(ban);
        kafkaEventProducer.sendUserBanned(user.getEmail(), dto.reason());

        log.info("User {} banned successfully", dto.userId());
    }
}
