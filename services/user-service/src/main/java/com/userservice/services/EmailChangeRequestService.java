package com.userservice.services;

import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.userservice.repositories.EmailChangeRequestRepository;
import com.userservice.services.grpc.GrpcCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailChangeRequestService {
    private final EmailChangeRequestRepository emailChangeRequestRepository;
    private final GrpcCodeService grpcCodeService;
}
