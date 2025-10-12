package ru.xing.springcourse.petproject3rest.util;

import org.springframework.validation.BindingResult;

import java.util.List;

public class ErrorUtil {
    public static List<FieldErrorDto> mapErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(e -> new FieldErrorDto(e.getField(), e.getDefaultMessage()))
                .toList();
    }

    public static void throwErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MeasurementException(mapErrors(bindingResult));
        }
    }
}
