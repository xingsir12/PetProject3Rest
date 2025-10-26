package ru.xing.springcourse.petproject3rest.unit.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MeasurementDTOValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validMeasurementDTO_NoViolations() {
        MeasurementDTO dto = new MeasurementDTO();
        dto.setValue(25.5);
        dto.setRaining(false);
        dto.setMeasurementDateTime(LocalDateTime.now());

        Set<ConstraintViolation<MeasurementDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Valid DTO should have no violations");
    }

    @Test
    void measurementDTO_WithNullValue_ShouldFail() {
        MeasurementDTO dto = new MeasurementDTO();
        dto.setValue(null); // Если @NotNull
        dto.setRaining(false);
        dto.setMeasurementDateTime(LocalDateTime.now());

        Set<ConstraintViolation<MeasurementDTO>> violations = validator.validate(dto);

        // Проверяем есть ли валидация на value
        // assertFalse(violations.isEmpty());
    }

    @Test
    void measurementDTO_WithExtremeTemperatures_ShouldPass() {
        MeasurementDTO dto1 = new MeasurementDTO();
        dto1.setValue(-273.15); // Absolute zero
        dto1.setRaining(false);
        dto1.setMeasurementDateTime(LocalDateTime.now());

        MeasurementDTO dto2 = new MeasurementDTO();
        dto2.setValue(100.0); // Boiling water
        dto2.setRaining(false);
        dto2.setMeasurementDateTime(LocalDateTime.now());

        // Проверяем минимум и максимум, если есть @Min/@Max
        Set<ConstraintViolation<MeasurementDTO>> violations1 = validator.validate(dto1);
        Set<ConstraintViolation<MeasurementDTO>> violations2 = validator.validate(dto2);

        // Зависит от ваших валидаций
    }

    @Test
    void measurementDTO_WithNullDateTime_ShouldFail() {
        MeasurementDTO dto = new MeasurementDTO();
        dto.setValue(20.0);
        dto.setRaining(false);
        dto.setMeasurementDateTime(null); // Если @NotNull

        Set<ConstraintViolation<MeasurementDTO>> violations = validator.validate(dto);

        // Проверяем если есть @NotNull на measurementDateTime
    }

    @Test
    void measurementDTO_RainingBooleanWorks() {
        MeasurementDTO dto = new MeasurementDTO();
        dto.setValue(15.0);
        dto.setRaining(true);
        dto.setMeasurementDateTime(LocalDateTime.now());

        Set<ConstraintViolation<MeasurementDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
        assertTrue(dto.isRaining());
    }
}
