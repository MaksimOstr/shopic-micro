package com.productservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.productservice.exceptions.NotFoundException;
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

    @JsonCreator
    public static SortDirectionEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return SortDirectionEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Provided sort direction " + name + " is not supported");
        }
    }
}
