package com.arra.book.exceptionHandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCode {

    // enum-constants
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No specific error code assigned"),
    INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "The provided current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "The new password and its confirmation do not match"),
    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "The user account is locked due to security restrictions"),
    ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "The user account has been disabled by an administrator"),
    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Invalid login credentials: username or password is incorrect");


    // attributes for the enum-constants
    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    BusinessErrorCode(int code, HttpStatus status, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = status;
    }


}
