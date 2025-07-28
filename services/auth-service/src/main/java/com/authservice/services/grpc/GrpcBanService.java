package com.authservice.services.grpc;


import com.shopic.grpc.banservice.BanServiceGrpc;
import com.shopic.grpc.banservice.CheckUserBanRequest;
import com.shopic.grpc.banservice.CheckUserBanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcBanService {
    private final BanServiceGrpc.BanServiceBlockingStub blockingStub;

    public CheckUserBanResponse checkUserBan(long userId){
        CheckUserBanRequest request = CheckUserBanRequest.newBuilder()
                .setUserId(userId)
                .build();

        return blockingStub.checkUserBan(request);
    }
}
