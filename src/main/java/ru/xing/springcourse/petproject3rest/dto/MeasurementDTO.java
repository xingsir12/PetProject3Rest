package ru.xing.springcourse.petproject3rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeasurementDTO {

    @Schema(
            description = "Temperature or other measurement value",
            example = "23.5",
            minimum = "-100",
            maximum = "100"
    )
    @NotNull(message = "Value should not be null")
    @DecimalMin(value = "-100.0", message = "Value must be greater than -100.0")
    @DecimalMax(value = "100.0", message = "Value must be less than 100.0")
    private Double value;

    @Schema(
            description = "Whether it was raining during measurement",
            example = "false"
    )
    @NotNull(message = "Raining field must be specified")
    private Boolean raining;

    @Schema(
            description = "Measurement timestamp (auto-generated if not provided)",
            example = "2024-10-17T14:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime measurementDateTime;
}
