package com.userservice.services;

import com.userservice.dto.CreateProfileDto;
import com.userservice.entity.Profile;
import com.userservice.entity.User;
import com.userservice.exceptions.EntityAlreadyExistsException;
import com.userservice.mapper.ProfileMapper;
import com.userservice.repositories.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Transactional
    public Profile createProfile(CreateProfileDto dto, User user) {
        if(isProfileExist(user.getId())) {
            throw new EntityAlreadyExistsException("Profile already exists");
        }

        Profile profile = profileMapper.toProfile(dto, user);

        return profileRepository.save(profile);
    }

    public Profile getProfileByUserId(long userId) {
        return profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }

    private boolean isProfileExist(long userId) {
        return profileRepository.existsByUser_Id(userId);
    }
}
