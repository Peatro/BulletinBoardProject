package com.peatroxd.bulletinboardproject.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotFoundExceptionMessage {
    CATEGORY_NOT_FOUND("Category not found."),
    USER_NOT_FOUND("User not found.");
    private final String message;
}
