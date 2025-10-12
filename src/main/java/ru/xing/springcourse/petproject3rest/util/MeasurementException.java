package ru.xing.springcourse.petproject3rest.util;

import lombok.Getter;

import java.util.List;

@Getter
public class MeasurementException extends RuntimeException {

    private final List<FieldErrorDto> errors;

    public MeasurementException(List<FieldErrorDto> errors) {
        super("Validation failed");
        this.errors = errors;
    }

}
