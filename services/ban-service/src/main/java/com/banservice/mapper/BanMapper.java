package com.banservice.mapper;

import com.banservice.dto.BanDto;
import com.banservice.entity.Ban;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BanMapper {

    BanDto toBanDto(Ban ban);

    List<BanDto> toBanDtoList(List<Ban> bans);
}
