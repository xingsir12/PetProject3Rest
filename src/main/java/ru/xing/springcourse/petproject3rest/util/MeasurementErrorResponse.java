package ru.xing.springcourse.petproject3rest.util;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MeasurementErrorResponse {
    private String message;
    private long timestamp;

}
