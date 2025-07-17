package com.profileservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profileservice.dto.CreateProfileDto;
import com.profileservice.dto.event.ProfileCreationEvent;
import com.profileservice.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final ProfileService profileService;
    private final ObjectMapper objectMapper;
    private final ProfileMapper profileMapper;

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = {"user.oauth.registered", "user.local.registered"}, groupId = "profile-service")
    public void listenUserRegisteredEvent(String data, Acknowledgment ack) {
        try {
            ProfileCreationEvent event = objectMapper.readValue(data, ProfileCreationEvent.class);
            CreateProfileDto dto = profileMapper.toCreateDto(event);

            profileService.createProfile(dto);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            ack.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
