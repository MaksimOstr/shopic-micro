package com.profileservice.service;

import com.profileservice.dto.CreateProfileDto;
import com.profileservice.dto.request.UpdateProfileRequest;
import com.profileservice.entity.Profile;
import com.profileservice.exception.AlreadyExistsException;
import com.profileservice.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    @Transactional
    public void createProfile(CreateProfileDto dto) {
        if (isProfileExist(dto.userId())) {
            throw new AlreadyExistsException("Profile already exists");
        }

        Profile profile = Profile.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .userId(dto.userId())
                .build();

        profileRepository.save(profile);
    }

    @Transactional
    public void editProfile(UpdateProfileRequest dto, long userId) {
        Profile profile = getProfileByUserId(userId);

        Optional.ofNullable(dto.firstName()).ifPresent(profile::setFirstName);
        Optional.ofNullable(dto.lastName()).ifPresent(profile::setLastName);
    }

    public Profile getProfileByUserId(long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }

    private boolean isProfileExist(long userId) {
        return profileRepository.existsByUserId(userId);
    }
}
