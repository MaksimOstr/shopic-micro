package com.reviewservice.mapper;

import com.reviewservice.dto.RatingDto;
import com.shopic.grpc.reviewservice.ProductRating;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    ProductRating toProductRating(RatingDto dto);

    List<ProductRating> toProductRatingList(List<RatingDto> dtoList);
}
