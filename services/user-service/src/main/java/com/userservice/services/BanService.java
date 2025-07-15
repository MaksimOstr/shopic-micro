package com.userservice.services;

import com.userservice.dto.BanDto;
import com.userservice.dto.request.BanParams;
import com.userservice.dto.request.BanRequest;
import com.userservice.entity.Ban;
import com.userservice.entity.User;
import com.userservice.exceptions.NotFoundException;
import com.userservice.mapper.BanMapper;
import com.userservice.repositories.BanRepository;
import com.userservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.userservice.utils.SpecificationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BanService {
    private final BanRepository banRepository;
    private final QueryUserService queryUserService;
    private final KafkaEventProducer kafkaEventProducer;
    private final BanMapper banMapper;

    @Transactional
    public void banUser(BanRequest dto, long bannerId) {
        User user = queryUserService.findById(dto.userId());

        if(!user.getIsVerified()) {
            throw new IllegalStateException("User is not verified");
        }

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

    public Page<BanDto> getBans(BanParams params, Pageable pageable) {
        Specification<Ban> spec = SpecificationUtils.<Ban>hasChild("user", params.userId())
                .and(gte("banTo", params.bannedFrom()))
                .and(lte("banTo", params.bannedTo()))
                .and(hasChild("bannedBy", params.bannedBy()))
                .and(hasChild("unbannedBy", params.unbannedBy()))
                .and(is("isActive", params.isActive()));

        Page<Ban> banPage = banRepository.findAll(spec, pageable);
        List<Ban> banList = banPage.getContent();
        List<BanDto> banDtoList = banMapper.toBanDtoList(banList);

        return new PageImpl<>(banDtoList, pageable, banPage.getTotalElements());
    }

    public BanDto getBan(long banId) {
        Ban ban = findById(banId);

        return banMapper.toBanDto(ban);
    }



    @Transactional
    public void unbanUser(long banId, long unbannerId) {
        Ban ban = findById(banId);
        User unbanner = queryUserService.findById(unbannerId);

        ban.setUnbannedBy(unbanner);
        ban.setActive(false);
        ban.setBanTo(Instant.now());
    }

    private Ban findById(long id) {
        return banRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ban not found"));
    }


    @Scheduled(fixedRate = 1000 * 60)
    public void updateExpiredBans() {
        banRepository.deactivateExpiredBans();
        log.info("Expired bans have been updated");
    }
}
