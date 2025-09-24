package com.productservice.mapper;

import com.productservice.dto.UserBrandDto;
import com.productservice.entity.Brand;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    UserBrandDto toUserBrandDto(Brand brand);

    List<UserBrandDto> toUserBrandDtoList(List<Brand> brands);
}
