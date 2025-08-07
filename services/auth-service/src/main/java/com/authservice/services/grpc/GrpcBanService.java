package com.authservice.services.grpc;


import com.authservice.exceptions.ExternalServiceUnavailableException;
import com.shopic.grpc.banservice.BanServiceGrpc;
import com.shopic.grpc.banservice.CheckUserBanRequest;
import com.shopic.grpc.banservice.CheckUserBanResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcBanService {
    private final BanServiceGrpc.BanServiceBlockingStub blockingStub;

    @CircuitBreaker(name = "ban-service", fallbackMethod = "fallbackMethod")
    public CheckUserBanResponse checkUserBan(long userId) {
        log.info("checkUserBan");

        CheckUserBanRequest request = CheckUserBanRequest.newBuilder()
                .setUserId(userId)
                .build();

        return blockingStub.checkUserBan(request);
    }

    public CheckUserBanResponse fallbackMethod(long userId, Throwable e) {
        log.error("fallbackMethod: ban-service is unavailable");
        throw new ExternalServiceUnavailableException("Something went wrong. Try again later");
    }
}
