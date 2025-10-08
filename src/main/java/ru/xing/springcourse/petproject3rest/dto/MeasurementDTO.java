package ru.xing.springcourse.petproject3rest.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeasurementDTO {
    private String value;
    private boolean isRaining;
    private LocalDateTime measurementDateTime;
}
