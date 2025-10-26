package ru.xing.springcourse.petproject3rest.unit.exception;

import org.junit.jupiter.api.Test;
import ru.xing.springcourse.petproject3rest.util.BusinessException;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessExceptionTest {
    @Test
    void createBusinessException_WithMessage() {
        String message = "Test error message";

        BusinessException exception = new BusinessException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void businessException_IsRuntimeException() {
        BusinessException exception = new BusinessException("Error");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void businessException_CanBeThrown() {
        assertThrows(BusinessException.class, () -> {
            throw new BusinessException("Something went wrong");
        });
    }

    @Test
    void businessException_MessageIsCorrect() {
        String expectedMessage = "Sensor not found: TestSensor";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            throw new BusinessException(expectedMessage);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }
}
