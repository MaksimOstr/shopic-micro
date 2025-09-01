package com.profileservice.service;

import com.profileservice.dto.CreateProfileDto;
import com.profileservice.dto.ProfileDto;
import com.profileservice.dto.request.ProfileParams;
import com.profileservice.dto.request.UpdateProfileRequest;
import com.profileservice.entity.Profile;
import com.profileservice.exception.AlreadyExistsException;
import com.profileservice.mapper.ProfileMapper;
import com.profileservice.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.profileservice.utils.SpecificationUtils.equalsLong;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

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
        try {
            Profile profile = getProfileByUserId(userId);

            Optional.ofNullable(dto.firstName()).ifPresent(profile::setFirstName);
            Optional.ofNullable(dto.lastName()).ifPresent(profile::setLastName);
        } catch (NotFoundException e) {
            CreateProfileDto profileDto = new CreateProfileDto(
                    dto.firstName(),
                    dto.lastName(),
                    userId
            );

            createProfile(profileDto);
        }
    }

    public Page<ProfileDto> getProfileDtoPage(ProfileParams params, Pageable pageable) {
        Specification<Profile> spec = equalsLong("userId", params.userId());

        Page<Profile> profilePage = profileRepository.findAll(spec, pageable);
        List<Profile> profiles = profilePage.getContent();
        List<ProfileDto> profileDtoList = profileMapper.toDtoList(profiles);

        return new PageImpl<>(profileDtoList, pageable, profilePage.getTotalElements());
    }

    public ProfileDto getProfileDtoById(long id) {
        Profile profile = getProfileById(id);

        return profileMapper.toDto(profile);
    }

    public Profile getProfileByUserId(long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }


    private Profile getProfileById(long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }


    private boolean isProfileExist(long userId) {
        return profileRepository.existsByUserId(userId);
    }
}
