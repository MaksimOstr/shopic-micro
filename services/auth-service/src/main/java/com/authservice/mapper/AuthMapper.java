package com.authservice.mapper;

import com.authservice.dto.response.RegisterResponseDto;
import com.shopic.grpc.userservice.CreateLocalUserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TimeStampMapper.class})
public interface AuthMapper {

    @Mapping(target = "cratedAt", source = "createdAt")
    RegisterResponseDto toRegisterResponseDto(CreateLocalUserResponse response);
}
