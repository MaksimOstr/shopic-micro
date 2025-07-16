package com.userservice.services;

import com.userservice.dto.CreateProfileDto;
import com.userservice.dto.request.ProfileParams;
import com.userservice.entity.Profile;
import com.userservice.entity.User;
import com.userservice.exceptions.AlreadyExistsException;
import com.userservice.mapper.ProfileMapper;
import com.userservice.repositories.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Transactional
    public Profile createProfile(CreateProfileDto dto, User user) {
        if(isProfileExist(user.getId())) {
            throw new AlreadyExistsException("Profile already exists");
        }

        Profile profile = profileMapper.toProfile(dto, user);

        return profileRepository.save(profile);
    }

    @Transactional
    public void editProfile(ProfileParams param, long userId) {
        Profile profile = getProfileByUserId(userId);

        Optional.ofNullable(param.firstName()).ifPresent(profile::setFirstName);
        Optional.ofNullable(param.lastName()).ifPresent(profile::setLastName);
    }

    public Profile getProfileByUserId(long userId) {
        return profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }

    private boolean isProfileExist(long userId) {
        return profileRepository.existsByUser_Id(userId);
    }
}
