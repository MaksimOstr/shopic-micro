package com.banservice.service.grpc;

import com.banservice.service.BanService;
import com.shopic.grpc.banservice.BanServiceGrpc;
import com.shopic.grpc.banservice.CheckUserBanRequest;
import com.shopic.grpc.banservice.CheckUserBanResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcBanService extends BanServiceGrpc.BanServiceImplBase {
    private final BanService banService;

    public void checkUserBan(CheckUserBanRequest request, StreamObserver<CheckUserBanResponse> responseObserver) {
        log.info("checkUserBan");

        boolean isUserBanned = banService.isUserBanned(request.getUserId());

        CheckUserBanResponse response = CheckUserBanResponse.newBuilder()
                .setIsBanned(isUserBanned)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
