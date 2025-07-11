package com.reviewservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.reviewservice.exception.NotFoundException;

public enum ReportStatus {
    PENDING,
    APPROVED,
    REJECTED;

    @JsonCreator
    public static ReportStatus fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return ReportStatus.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
