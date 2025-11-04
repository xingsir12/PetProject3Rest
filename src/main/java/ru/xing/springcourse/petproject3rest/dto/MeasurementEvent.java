package ru.xing.springcourse.petproject3rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeasurementEvent {
    private String sensorName;
    private Double temperature;
    private Boolean isRaining;
    private LocalDateTime timestamp;
}
