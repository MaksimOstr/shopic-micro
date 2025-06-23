package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import com.shopic.grpc.codeservice.ValidateCodeResponse;
import com.userservice.dto.request.ChangeEmailRequest;
import com.userservice.entity.EmailChangeRequest;
import com.userservice.entity.User;
import com.userservice.exceptions.EntityAlreadyExistsException;
import com.userservice.exceptions.NotFoundException;
import com.userservice.projection.UserEmailAndPasswordProjection;
import com.userservice.repositories.EmailChangeRequestRepository;
import com.userservice.services.grpc.GrpcCodeService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailChangeRequestService {
    private final EmailChangeRequestRepository emailChangeRequestRepository;
    private final GrpcCodeService grpcCodeService;
    private final QueryUserService queryUserService;
    private final PasswordService passwordService;
    private final KafkaEventProducer kafkaEventProducer;
    private final EntityManager entityManager;

    @Transactional
    public void createRequest(ChangeEmailRequest dto, long userId) throws JsonProcessingException {
        boolean isUserExists = queryUserService.isUserExist(dto.email());
        if (isUserExists) {
            throw new EntityAlreadyExistsException("This email already exists");
        }

        UserEmailAndPasswordProjection user = queryUserService.getUserEmailAndPassword(userId);
        boolean isPasswordEqual = passwordService.comparePassword(user.getPassword(), dto.password());

        if (!isPasswordEqual) {
            throw new IllegalArgumentException("Password doesn't match");
        }

        createEmailChangeRequest(userId, dto.email());

        CreateCodeResponse response = grpcCodeService.getEmailChangeCode(userId);

        kafkaEventProducer.requestEmailChange(response.getCode(), dto.email());
    }

    @Transactional
    public void changeEmail(String code) {
        ValidateCodeResponse response = grpcCodeService.validateEmailChangeCode(code);
        User user = queryUserService.findById(response.getUserId());
        EmailChangeRequest changeRequest = emailChangeRequestRepository.findByUser_Id(response.getUserId())
                .orElseThrow(() -> new NotFoundException("No email change request found"));

        user.setEmail(changeRequest.getNewEmail());

        emailChangeRequestRepository.delete(changeRequest);
    }

    private void createEmailChangeRequest(long userId, String newEmail) {
        emailChangeRequestRepository.findByUser_Id(userId)
                .ifPresent(emailChangeRequestRepository::delete);

        EmailChangeRequest newEmailChangeRequest = EmailChangeRequest.builder()
                .newEmail(newEmail)
                .user(entityManager.getReference(User.class, userId))
                .build();

        emailChangeRequestRepository.save(newEmailChangeRequest);
    }


}
