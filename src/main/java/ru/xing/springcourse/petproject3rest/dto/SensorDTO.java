package ru.xing.springcourse.petproject3rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sensor registration/response data")
public class SensorDTO {

    @Schema(
            description = "Sensor name",
            example = "Sensor_Home",
            minLength = 3,
            maxLength = 30
    )
    @NotBlank(message = "Name should not be empty")
    @Size(min = 3, max = 30, message = "Name should be between 3 and 30 characters")
    private String name;

    @Schema(
            description = "List of measurements for this sensor",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<MeasurementDTO> measurements;
}
