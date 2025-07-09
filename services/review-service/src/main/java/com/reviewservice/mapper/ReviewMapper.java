package com.reviewservice.mapper;

import com.reviewservice.dto.ReviewDto;
import com.reviewservice.entity.Review;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDto toDto(Review review);

    List<ReviewDto> toDto(List<Review> reviews);
}
