package com.authservice.mapper;


import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TimeStampMapper.class})
public interface AuthMapper {
}
