package com.banservice.mapper;

import com.banservice.dto.BanDto;
import com.banservice.entity.Ban;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BanMapper {

    @Mapping(target = "bannedBy", source = "bannedBy.id")
    @Mapping(target = "unbannedBy", source = "unbannedBy.id")
    BanDto toBanDto(Ban ban);

    List<BanDto> toBanDtoList(List<Ban> bans);
}
