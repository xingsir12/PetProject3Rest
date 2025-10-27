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
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;
import ru.xing.springcourse.petproject3rest.services.MeasurementService;
import ru.xing.springcourse.petproject3rest.util.BusinessException;
import ru.xing.springcourse.petproject3rest.util.MeasurementMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeasurementServiceTest {
    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private MeasurementMapper measurementMapper;

    @InjectMocks
    private MeasurementService measurementService;

    private Sensor sensor;
    private Measurement measurement;
    private MeasurementDTO measurementDTO;

    @BeforeEach
    void setUp() {
        sensor = new Sensor();
        sensor.setId(1);
        sensor.setName("TestSensor");

        measurement = new Measurement();
        measurement.setId(1);
        measurement.setValue(25.5);
        measurement.setRaining(false);
        measurement.setMeasurementDateTime(LocalDateTime.now());
        measurement.setSensor(sensor);

        measurementDTO = new MeasurementDTO();
        measurementDTO.setValue(25.5);
        measurementDTO.setRaining(false);
        measurementDTO.setMeasurementDateTime(LocalDateTime.now());
    }

    @Test
    void addMeasurement_Success() {
        when(sensorRepository.findByName("TestSensor")).thenReturn(Optional.of(sensor));
        when(measurementMapper.toEntity(any(MeasurementDTO.class), any(Sensor.class)))
                .thenReturn(measurement);
        when(measurementRepository.save(any(Measurement.class))).thenReturn(measurement);

        measurementService.addMeasurement("TestSensor", measurementDTO);

        verify(sensorRepository, times(1)).findByName("TestSensor");
        verify(measurementMapper, times(1)).toEntity(measurementDTO, sensor);
        verify(measurementRepository, times(1)).save(measurement);
    }

    @Test
    void addMeasurement_ThrowsException_WhenSensorNotFound() {
        when(sensorRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> measurementService.addMeasurement("NonExistent", measurementDTO));

        assertEquals("Sensor not found: NonExistent", exception.getMessage());
        verify(sensorRepository, times(1)).findByName("NonExistent");
        verify(measurementRepository, never()).save(any());
    }

    @Test
    void addMeasurement_WithRainingTrue() {
        measurementDTO.setRaining(true);
        measurement.setRaining(true);

        when(sensorRepository.findByName("TestSensor")).thenReturn(Optional.of(sensor));
        when(measurementMapper.toEntity(any(MeasurementDTO.class), any(Sensor.class)))
                .thenReturn(measurement);
        when(measurementRepository.save(any(Measurement.class))).thenReturn(measurement);

        measurementService.addMeasurement("TestSensor", measurementDTO);

        verify(measurementRepository, times(1)).save(measurement);
        assertTrue(measurement.isRaining());
    }

    @Test
    void getAllMeasurements_ReturnsPageOfMeasurements() {
        List<Measurement> measurementList = Arrays.asList(measurement);
        Page<Measurement> measurementPage = new PageImpl<>(measurementList);
        Pageable pageable = PageRequest.of(0, 10);

        when(measurementRepository.findAll(pageable)).thenReturn(measurementPage);
        when(measurementMapper.toDTO(any(Measurement.class))).thenReturn(measurementDTO);

        Page<MeasurementDTO> result = measurementService.getAllMeasurements(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(25.5, result.getContent().get(0).getValue());
        verify(measurementRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllMeasurements_EmptyPage() {
        Page<Measurement> emptyPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 10);

        when(measurementRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<MeasurementDTO> result = measurementService.getAllMeasurements(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getMeasurementById_Success() {
        when(measurementRepository.findById(1)).thenReturn(Optional.of(measurement));
        when(measurementMapper.toDTO(measurement)).thenReturn(measurementDTO);

        MeasurementDTO result = measurementService.getMeasurementById(1);

        assertNotNull(result);
        assertEquals(25.5, result.getValue());
        assertFalse(result.getRaining());
        verify(measurementRepository, times(1)).findById(1);
    }

    @Test
    void getMeasurementById_ThrowsException_WhenNotFound() {
        when(measurementRepository.findById(999)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> measurementService.getMeasurementById(999));

        assertEquals("Measurement not found: 999", exception.getMessage());
        verify(measurementRepository, times(1)).findById(999);
    }

    @Test
    void countRainingMeasurements_ReturnsCorrectCount() {
        when(measurementRepository.countByRainingTrue()).thenReturn(42L);

        long result = measurementService.countRainingMeasurements();

        assertEquals(42L, result);
        verify(measurementRepository, times(1)).countByRainingTrue();
    }

    @Test
    void countRainingMeasurements_ReturnsZero_WhenNoRaining() {
        when(measurementRepository.countByRainingTrue()).thenReturn(0L);

        long result = measurementService.countRainingMeasurements();

        assertEquals(0L, result);
    }

    @Test
    void getRainingMeasurements_ReturnsOnlyRainingOnes() {
        measurement.setRaining(true);
        measurementDTO.setRaining(true);

        List<Measurement> rainingList = Arrays.asList(measurement);
        Page<Measurement> rainingPage = new PageImpl<>(rainingList);
        Pageable pageable = PageRequest.of(0, 20);

        when(measurementRepository.findByRainingTrue(pageable)).thenReturn(rainingPage);
        when(measurementMapper.toDTO(any(Measurement.class))).thenReturn(measurementDTO);

        Page<MeasurementDTO> result = measurementService.getRainingMeasurements(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getRaining());
        verify(measurementRepository, times(1)).findByRainingTrue(pageable);
    }

    @Test
    void getRainingMeasurements_EmptyPage_WhenNoRaining() {
        Page<Measurement> emptyPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 20);

        when(measurementRepository.findByRainingTrue(pageable)).thenReturn(emptyPage);

        Page<MeasurementDTO> result = measurementService.getRainingMeasurements(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }
}
