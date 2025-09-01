package com.profileservice.service.grpc;

import com.profileservice.entity.Profile;
import com.profileservice.service.ProfileService;
import com.shopic.grpc.profileservice.ProfileRequest;
import com.shopic.grpc.profileservice.ProfileResponse;
import com.shopic.grpc.profileservice.ProfileServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class ProfileGrpcService extends ProfileServiceGrpc.ProfileServiceImplBase {
    private final ProfileService profileService;

    @Override
    public void getUserProfile(ProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        Profile profile = profileService.getProfileByUserId(request.getUserId());

        ProfileResponse response = ProfileResponse.newBuilder()
                .setFirstName(profile.getFirstName())
                .setLastName(profile.getLastName())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
