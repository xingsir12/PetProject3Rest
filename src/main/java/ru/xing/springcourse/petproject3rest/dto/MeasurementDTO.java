package ru.xing.springcourse.petproject3rest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeasurementDTO {

    @NotNull(message = "Value should not be null")
    @DecimalMin(value = "-100.0", message = "Value must be greater than -100.0")
    @DecimalMax(value = "100.0", message = "Value must be less than 100.0")
    private Double value;

    @NotNull(message = "Raining field must be specified")
    private boolean isRaining;

    private LocalDateTime measurementDateTime;
}
