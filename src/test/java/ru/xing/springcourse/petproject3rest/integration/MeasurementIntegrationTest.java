package ru.xing.springcourse.petproject3rest.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MeasurementIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    private Sensor testSensor;

    @BeforeEach
    void setUp() {
        measurementRepository.deleteAll();
        sensorRepository.deleteAll();

        testSensor = new Sensor();
        testSensor.setName("testSensor");
        testSensor = sensorRepository.save(testSensor);
    }

    @Test
    void shouldAddMeasurement() {
        // Given
        MeasurementDTO measurementDTO = MeasurementDTO.builder()
                .value(23.5)
                .isRaining(false)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "user123");
        HttpEntity<MeasurementDTO> request = new HttpEntity<>(measurementDTO, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/measurements/add?sensorName=" + testSensor.getName(),
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Measurement> measurements = measurementRepository.findAll();
        assertThat(measurements).hasSize(1);
        assertThat(measurements.get(0).getValue()).isEqualTo(23.5);
        assertThat(measurements.get(0).isRaining()).isEqualTo(false);
    }

    @Test
    void shouldValidateMeasurementValue() {
        // Given - invalid value (> 100)
        MeasurementDTO measurementDTO = MeasurementDTO.builder()
                .value(150.5)
                .isRaining(false)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "user123");
        HttpEntity<MeasurementDTO> request = new HttpEntity<>(measurementDTO, headers);

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/measurements/add?sensorName=" + testSensor.getName(),
                request,
                Map.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Validation failed");
        assertThat(response.getBody().get("message")).isNotNull();
    }

    @Test
    void shouldGetRainingMeasurementCount() {
        // Given - create test measurements
        for (int i = 0; i < 10; i++) {
            Measurement m = Measurement.builder()
                    .sensor(testSensor)
                    .value(20)
                    .raining(i % 2 == 0) // 5 raining - not raining
                    .measurementDateTime(LocalDateTime.now())
                    .build();
            measurementRepository.save(m);
        }

        // When
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "user123");
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/measurements/raining/count", Map.class
        );


        //Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("count")).isEqualTo(5);
    }

}
