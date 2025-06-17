package com.example.imagedemo.common;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public enum Status {

    SUCCESS(HttpStatus.OK, "Operation successful"), NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"), BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"), UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized access"), FORBIDDEN(HttpStatus.FORBIDDEN, "Access forbidden"), INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"), VALIDATION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed"), CREATED(HttpStatus.CREATED, "Resource created"), UPDATED(HttpStatus.OK, "Resource updated"), DELETED(HttpStatus.OK, "Resource deleted");

    private final HttpStatus StatusCode;
    private final String StatusDescription;

    Status(HttpStatus httpStatus, String message) {
        this.StatusCode = httpStatus;
        this.StatusDescription = message;
    }
}
