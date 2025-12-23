package com.productservice.mapper;

import com.productservice.dto.AdminBrandDto;
import com.productservice.dto.UserBrandDto;
import com.productservice.entity.Brand;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    UserBrandDto toUserBrandDto(Brand brand);

    List<AdminBrandDto> toAdminBrandDto(List<Brand> brands);

    AdminBrandDto toAdminBrandDto(Brand brand);

    List<UserBrandDto> toUserBrandDtoList(List<Brand> brands);
}
