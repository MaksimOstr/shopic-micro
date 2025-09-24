package com.productservice.mapper;

import com.productservice.dto.UserCategoryDto;
import com.productservice.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    UserCategoryDto toUserCategoryDto(Category category);

    List<UserCategoryDto> toUserCategoryDtoList(List<Category> categories);
}
