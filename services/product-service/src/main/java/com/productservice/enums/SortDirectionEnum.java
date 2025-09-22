package com.productservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum SortDirectionEnum {
    ASC(Sort.Direction.ASC),
    DESC(Sort.Direction.DESC);

    private final Sort.Direction springDirection;

    public Sort.Direction toSpringDirection() {
        return springDirection;
    }
}
