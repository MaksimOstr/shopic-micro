package com.banservice.service.grpc;

import com.banservice.exception.NotFoundException;
import com.shopic.grpc.authservice.AuthServiceGrpc;
import com.shopic.grpc.authservice.UserForBanRequest;
import com.shopic.grpc.authservice.UserForBanResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcAuthService {
    private final AuthServiceGrpc.AuthServiceBlockingStub authGrpcService;


    public UserForBanResponse getUserForBan(long userId) {
        try {
            UserForBanRequest request = UserForBanRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            return authGrpcService.getUserForBan(request);
        } catch (StatusRuntimeException e) {
            if(e.getStatus() == Status.NOT_FOUND) {
                throw new NotFoundException(e.getStatus().getDescription());
            }

            throw e;
        }
    }

}
