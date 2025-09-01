package com.reviewservice.service.grpc;

import com.reviewservice.exception.NotFoundException;
import com.shopic.grpc.profileservice.ProfileRequest;
import com.shopic.grpc.profileservice.ProfileResponse;
import com.shopic.grpc.profileservice.ProfileServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcProfileService {
    private final ProfileServiceGrpc.ProfileServiceBlockingStub profileGrpcService;

    public ProfileResponse getUserProfile(long userId) {
        ProfileRequest request = ProfileRequest.newBuilder().setUserId(userId).build();

        try {
            return profileGrpcService.getUserProfile(request);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NotFoundException(e.getStatus().getDescription());
            }

            throw e;
        }
    }
}
