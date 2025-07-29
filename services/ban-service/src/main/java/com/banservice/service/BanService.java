package com.banservice.service;

import com.banservice.dto.BanDto;
import com.banservice.dto.request.BanParams;
import com.banservice.dto.request.BanRequest;
import com.banservice.entity.Ban;
import com.banservice.exception.NotFoundException;
import com.banservice.mapper.BanMapper;
import com.banservice.repository.BanRepository;
import com.banservice.service.grpc.GrpcAuthService;
import com.banservice.utils.SpecificationUtils;
import com.shopic.grpc.authservice.UserForBanResponse;
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
import java.util.List;
import java.util.Objects;

import static com.banservice.utils.SpecificationUtils.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class BanService {
    private final BanRepository banRepository;
    private final GrpcAuthService grpcAuthService;
    private final KafkaEventProducer kafkaEventProducer;
    private final BanMapper banMapper;

    @Transactional
    public void banUser(BanRequest dto, long bannerId) {
        boolean isBanExist = isUserBanned(dto.userId());

        if(isBanExist) {
            throw new IllegalStateException("User account is already locked");
        }

        UserForBanResponse response = grpcAuthService.getUserForBan(dto.userId());

        if(!response.getIsVerified()) {
            throw new IllegalStateException("User is not verified");
        }

        if(Objects.equals(dto.userId(), bannerId)) {
            throw new IllegalStateException("You can't ban yourself");
        }

        Ban ban = Ban.builder()
                .banTo(dto.banTo())
                .bannerId(bannerId)
                .userId(dto.userId())
                .reason(dto.reason())
                .isActive(true)
                .build();

        banRepository.save(ban);
        kafkaEventProducer.sendUserBanned(response.getEmail(), dto.reason());

        log.info("User {} banned successfully", dto.userId());
    }

    public boolean isUserBanned(long userId) {
        return banRepository.existsByUserIdAndIsActive(userId, true);
    }

    public Page<BanDto> getBans(BanParams params, Pageable pageable) {
        Specification<Ban> spec = SpecificationUtils.<Ban>hasChild("user", params.userId())
                .and(gte("banTo", params.bannedFrom()))
                .and(lte("banTo", params.bannedTo()))
                .and(hasChild("bannedBy", params.bannerId()))
                .and(hasChild("unbannedBy", params.unbannerId()))
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

        ban.setUnbannerId(unbannerId);
        ban.setIsActive(false);
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
