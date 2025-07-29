package com.profileservice.mapper;

import com.profileservice.dto.CreateProfileDto;
import com.profileservice.dto.ProfileDto;
import com.profileservice.dto.event.ProfileCreationEvent;
import com.profileservice.entity.Profile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    CreateProfileDto toCreateDto(ProfileCreationEvent event);


    ProfileDto toDto(Profile profile);

    List<ProfileDto> toDtoList(List<Profile> profiles);
}
