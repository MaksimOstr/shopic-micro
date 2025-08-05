package com.authservice.services.grpc;


import com.shopic.grpc.banservice.BanServiceGrpc;
import com.shopic.grpc.banservice.CheckUserBanRequest;
import com.shopic.grpc.banservice.CheckUserBanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcBanService {
    private final BanServiceGrpc.BanServiceBlockingStub blockingStub;

    public CheckUserBanResponse checkUserBan(long userId) {
        log.info("checkUserBan");

        CheckUserBanRequest request = CheckUserBanRequest.newBuilder()
                .setUserId(userId)
                .build();

        return blockingStub.checkUserBan(request);
    }
}
