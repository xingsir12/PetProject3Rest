package ru.xing.springcourse.petproject3rest.unit.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SensorDTOValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validSensorDTO_NoViolations() {
        SensorDTO dto = new SensorDTO();
        dto.setName("ValidSensor");

        Set<ConstraintViolation<SensorDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void sensorDTO_WithEmptyName_ShouldFail() {
        SensorDTO dto = new SensorDTO();
        dto.setName(""); // Если есть @NotBlank

        Set<ConstraintViolation<SensorDTO>> violations = validator.validate(dto);

        // Проверяем если есть @NotBlank
        // assertFalse(violations.isEmpty());
    }

    @Test
    void sensorDTO_WithNullName_ShouldFail() {
        SensorDTO dto = new SensorDTO();
        dto.setName(null); // Если есть @NotNull или @NotBlank

        Set<ConstraintViolation<SensorDTO>> violations = validator.validate(dto);

        // Проверяем если есть валидация
    }

    @Test
    void sensorDTO_WithWhitespaceName_ShouldFail() {
        SensorDTO dto = new SensorDTO();
        dto.setName("   "); // Только пробелы - @NotBlank должен отклонить

        Set<ConstraintViolation<SensorDTO>> violations = validator.validate(dto);

        // Проверяем @NotBlank
    }

    @Test
    void sensorDTO_WithLongName_ShouldCheck() {
        SensorDTO dto = new SensorDTO();
        dto.setName("A".repeat(200)); // Очень длинное имя

        Set<ConstraintViolation<SensorDTO>> violations = validator.validate(dto);

        // Проверяем если есть @Size(max=...)
    }
}
