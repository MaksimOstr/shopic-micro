package com.authservice.services.grpc;

import com.authservice.projection.user.UserForBanProjection;
import com.authservice.services.user.UserQueryService;
import com.shopic.grpc.authservice.AuthServiceGrpc;
import com.shopic.grpc.authservice.UserForBanRequest;
import com.shopic.grpc.authservice.UserForBanResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcAuthService extends AuthServiceGrpc.AuthServiceImplBase {
    private final UserQueryService userQueryService;

    @Override
    public void getUserForBan(UserForBanRequest request, StreamObserver<UserForBanResponse> responseObserver) {
        UserForBanProjection userForBan = userQueryService.getUserForBan(request.getUserId());

        UserForBanResponse response = UserForBanResponse.newBuilder()
                .setEmail(userForBan.email())
                .setIsVerified(userForBan.isVerified())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
