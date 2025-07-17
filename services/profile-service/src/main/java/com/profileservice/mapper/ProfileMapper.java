package com.profileservice.mapper;

import com.profileservice.dto.CreateProfileDto;
import com.profileservice.dto.event.ProfileCreationEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    CreateProfileDto toCreateDto(ProfileCreationEvent event);
}
