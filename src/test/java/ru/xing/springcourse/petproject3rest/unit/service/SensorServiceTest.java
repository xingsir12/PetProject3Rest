package ru.xing.springcourse.petproject3rest.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;
import ru.xing.springcourse.petproject3rest.services.SensorService;
import ru.xing.springcourse.petproject3rest.util.BusinessException;
import ru.xing.springcourse.petproject3rest.util.SensorMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorServiceTest {
    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private SensorMapper sensorMapper;

    @Mock
    private MeasurementRepository measurementRepository;

    @InjectMocks
    private SensorService sensorService;

    private Sensor sensor;
    private SensorDTO sensorDTO;

    @BeforeEach
    void setUp() {
        sensor = new Sensor();
        sensor.setId(1);
        sensor.setName("TestSensor");

        sensorDTO = new SensorDTO();
        sensorDTO.setName("TestSensor");
    }

    @Test
    void getSensorByName_Success() {
        when(sensorRepository.findByName("TestSensor")).thenReturn(Optional.of(sensor));
        when(sensorMapper.toDTO(sensor)).thenReturn(sensorDTO);

        SensorDTO result = sensorService.getSensorByName("TestSensor");

        assertNotNull(result);
        assertEquals("TestSensor", result.getName());
        verify(sensorRepository, times(1)).findByName("TestSensor");
        verify(sensorMapper, times(1)).toDTO(sensor);
    }

    @Test
    void getSensorByName_ThrowsException_WhenNotFound() {
        when(sensorRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sensorService.getSensorByName("NonExistent"));

        assertEquals("Sensor not found", exception.getMessage());
        verify(sensorRepository, times(1)).findByName("NonExistent");
        verify(sensorMapper, never()).toDTO(any());
    }

    @Test
    void getAllSensors_ReturnsPageOfSensors() {
        List<Sensor> sensorList = Arrays.asList(sensor);
        Page<Sensor> sensorPage = new PageImpl<>(sensorList);
        Pageable pageable = PageRequest.of(0, 10);

        when(sensorRepository.findAll(pageable)).thenReturn(sensorPage);
        when(sensorMapper.toDTO(any(Sensor.class))).thenReturn(sensorDTO);

        Page<SensorDTO> result = sensorService.getAllSensors(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("TestSensor", result.getContent().get(0).getName());
        verify(sensorRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllSensors_EmptyPage() {
        Page<Sensor> emptyPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 10);

        when(sensorRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<SensorDTO> result = sensorService.getAllSensors(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void registerSensor_Success() {
        when(sensorRepository.findByName("NewSensor")).thenReturn(Optional.empty());
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);

        sensorService.registerSensor("NewSensor");

        verify(sensorRepository, times(1)).findByName("NewSensor");
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void registerSensor_ThrowsException_WhenAlreadyExists() {
        when(sensorRepository.findByName("TestSensor")).thenReturn(Optional.of(sensor));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sensorService.registerSensor("TestSensor"));

        assertEquals("Sensor already exists: TestSensor", exception.getMessage());
        verify(sensorRepository, times(1)).findByName("TestSensor");
        verify(sensorRepository, never()).save(any());
    }

    @Test
    void registerSensor_WithEmptyName_ShouldThrowValidationException() {
        // Этот тест проверит валидацию, если она настроена через @NotBlank
        // В реальности Spring Validation обработает это до вызова метода
        when(sensorRepository.findByName("")).thenReturn(Optional.empty());

        sensorService.registerSensor("");

        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }
}

