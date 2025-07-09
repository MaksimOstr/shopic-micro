package com.reviewservice.mapper;

import com.reviewservice.dto.ReviewCommentDto;
import com.reviewservice.entity.ReviewComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewCommentMapper {

    @Mapping(target = "reviewId", source = "review.id")
    ReviewCommentDto toDto(ReviewComment comment);

    List<ReviewCommentDto> toDtoList(List<ReviewComment> comments);
}
