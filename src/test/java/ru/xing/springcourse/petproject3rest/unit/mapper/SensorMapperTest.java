package ru.xing.springcourse.petproject3rest.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.util.MeasurementMapper;
import ru.xing.springcourse.petproject3rest.util.SensorMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SensorMapperTest {
    @Mock
    private MeasurementMapper measurementMapper;

    private SensorMapper sensorMapper;

    @BeforeEach
    void setUp() {
        sensorMapper = new SensorMapper(measurementMapper);
    }

    @Test
    void toDTO_ConvertsEntityToDTO_WithoutMeasurements() {
        Sensor sensor = new Sensor();
        sensor.setId(1);
        sensor.setName("TestSensor");
        sensor.setMeasurements(new ArrayList<>());

        SensorDTO dto = sensorMapper.toDTO(sensor);

        assertNotNull(dto);
        assertEquals("TestSensor", dto.getName());
    }

    @Test
    void toDTO_ThrowsNullPointerException_WhenInputIsNull() {
        // Ваш mapper не обрабатывает null, поэтому ожидаем NPE
        assertThrows(NullPointerException.class, () -> {
            sensorMapper.toDTO(null);
        });
    }

    @Test
    void toDTO_ConvertsEntityWithNullName() {
        Sensor sensor = new Sensor();
        sensor.setId(1);
        sensor.setName(null);
        sensor.setMeasurements(new ArrayList<>());

        SensorDTO dto = sensorMapper.toDTO(sensor);

        assertNotNull(dto);
        assertNull(dto.getName());
    }

    @Test
    void toDTO_WithMeasurements_CallsMeasurementMapper() {
        Sensor sensor = new Sensor();
        sensor.setId(1);
        sensor.setName("SensorWithData");

        Measurement measurement = new Measurement();
        measurement.setId(1);
        measurement.setValue(25.5);
        measurement.setRaining(false);
        measurement.setMeasurementDateTime(LocalDateTime.now());
        sensor.setMeasurements(List.of(measurement));

        MeasurementDTO measurementDTO = new MeasurementDTO();
        measurementDTO.setValue(25.5);
        measurementDTO.setRaining(false);
        when(measurementMapper.toDTO(measurement)).thenReturn(measurementDTO);

        SensorDTO dto = sensorMapper.toDTO(sensor);

        assertNotNull(dto);
        assertEquals("SensorWithData", dto.getName());
        assertNotNull(dto.getMeasurements());
        assertEquals(1, dto.getMeasurements().size());
        assertEquals(25.5, dto.getMeasurements().get(0).getValue());
    }

    @Test
    void toDTO_WithEmptyMeasurementsList() {
        Sensor sensor = new Sensor();
        sensor.setId(1);
        sensor.setName("EmptySensor");
        sensor.setMeasurements(new ArrayList<>());

        SensorDTO dto = sensorMapper.toDTO(sensor);

        assertNotNull(dto);
        assertEquals("EmptySensor", dto.getName());
        assertNotNull(dto.getMeasurements());
        assertTrue(dto.getMeasurements().isEmpty());
    }

    @Test
    void toEntity_ConvertsDTOToEntity() {
        SensorDTO dto = new SensorDTO();
        dto.setName("NewSensor");

        Sensor entity = sensorMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("NewSensor", entity.getName());
    }

    @Test
    void toEntity_ThrowsNullPointerException_WhenInputIsNull() {
        // Ваш mapper не обрабатывает null, поэтому ожидаем NPE
        assertThrows(NullPointerException.class, () -> {
            sensorMapper.toEntity(null);
        });
    }

    @Test
    void toEntity_HandlesEmptyName() {
        SensorDTO dto = new SensorDTO();
        dto.setName("");

        Sensor entity = sensorMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("", entity.getName());
    }

    @Test
    void toEntity_HandlesNullName() {
        SensorDTO dto = new SensorDTO();
        dto.setName(null);

        Sensor entity = sensorMapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getName());
    }
}
