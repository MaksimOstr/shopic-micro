package com.banservice.service.grpc;

import com.banservice.exception.ExternalServiceUnavailableException;
import com.banservice.exception.NotFoundException;
import com.shopic.grpc.authservice.AuthServiceGrpc;
import com.shopic.grpc.authservice.UserForBanRequest;
import com.shopic.grpc.authservice.UserForBanResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcAuthService {
    private final AuthServiceGrpc.AuthServiceBlockingStub authGrpcService;

    @CircuitBreaker(name = "auth-service", fallbackMethod = "getUserForBanFallback")
    public UserForBanResponse getUserForBan(long userId) {
        UserForBanRequest request = UserForBanRequest.newBuilder()
                .setUserId(userId)
                .build();

        return authGrpcService.getUserForBan(request);
    }

    public UserForBanResponse getUserForBanFallback(long userId, Throwable e) {
        if (e instanceof StatusRuntimeException exception) {
            if (exception.getStatus() == Status.NOT_FOUND) {
                throw new NotFoundException(exception.getStatus().getDescription());
            }
        }

        throw new ExternalServiceUnavailableException("Something went wrong. Try again later");
    }

}
