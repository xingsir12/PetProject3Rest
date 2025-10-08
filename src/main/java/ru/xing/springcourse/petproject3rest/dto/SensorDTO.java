package ru.xing.springcourse.petproject3rest.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorDTO {
    private String name;
    private List<MeasurementDTO> measurements;
}
