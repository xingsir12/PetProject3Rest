package ru.xing.springcourse.petproject3rest.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.util.MeasurementMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MeasurementMapperTest {
    private MeasurementMapper measurementMapper;
    private Sensor sensor;

    @BeforeEach
    void setUp() {
        measurementMapper = new MeasurementMapper();

        sensor = new Sensor();
        sensor.setId(1);
        sensor.setName("TestSensor");
    }

    @Test
    void toEntity_ConvertsDTOToEntity() {
        MeasurementDTO dto = new MeasurementDTO();
        dto.setValue(23.5);
        dto.setRaining(true);
        dto.setMeasurementDateTime(LocalDateTime.of(2024, 10, 26, 12, 0));

        Measurement entity = measurementMapper.toEntity(dto, sensor);

        assertNotNull(entity);
        assertEquals(23.5, entity.getValue());
        assertTrue(entity.isRaining());
        assertEquals(LocalDateTime.of(2024, 10, 26, 12, 0), entity.getMeasurementDateTime());
        assertEquals(sensor, entity.getSensor());
    }

    @Test
    void toEntity_HandlesNullDTO() {
        assertThrows(NullPointerException.class, () -> measurementMapper.toEntity(null, null));
    }

    @Test
    void toEntity_HandlesNullSensor() {
        MeasurementDTO dto = new MeasurementDTO();
        dto.setValue(20.0);
        dto.setRaining(false);
        dto.setMeasurementDateTime(LocalDateTime.now());

        Measurement entity = measurementMapper.toEntity(dto, null);

        assertNotNull(entity);
        assertNull(entity.getSensor());
    }

    @Test
    void toEntity_WithNegativeTemperature() {
        MeasurementDTO dto = new MeasurementDTO();
        dto.setValue(-15.5);
        dto.setRaining(false);
        dto.setMeasurementDateTime(LocalDateTime.now());

        Measurement entity = measurementMapper.toEntity(dto, sensor);

        assertNotNull(entity);
        assertEquals(-15.5, entity.getValue());
    }

    @Test
    void toEntity_WithExtremeTemperatures() {
        MeasurementDTO dto = new MeasurementDTO();
        dto.setValue(-50.0); // Extreme cold
        dto.setRaining(false);
        dto.setMeasurementDateTime(LocalDateTime.now());

        Measurement entity = measurementMapper.toEntity(dto, sensor);

        assertEquals(-50.0, entity.getValue());
    }

    @Test
    void toDTO_ConvertsEntityToDTO() {
        Measurement entity = new Measurement();
        entity.setId(1);
        entity.setValue(18.3);
        entity.setRaining(false);
        entity.setMeasurementDateTime(LocalDateTime.of(2024, 10, 26, 15, 30));
        entity.setSensor(sensor);

        MeasurementDTO dto = measurementMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(18.3, dto.getValue());
        assertFalse(dto.getRaining());
        assertEquals(LocalDateTime.of(2024, 10, 26, 15, 30), dto.getMeasurementDateTime());
    }

    @Test
    void toDTO_HandlesNullInput() {
        assertThrows(NullPointerException.class, () -> measurementMapper.toDTO(null));
    }

    @Test
    void toDTO_HandlesRainingTrue() {
        Measurement entity = new Measurement();
        entity.setValue(22.0);
        entity.setRaining(true);
        entity.setMeasurementDateTime(LocalDateTime.now());

        MeasurementDTO dto = measurementMapper.toDTO(entity);

        assertTrue(dto.getRaining());
    }
}
