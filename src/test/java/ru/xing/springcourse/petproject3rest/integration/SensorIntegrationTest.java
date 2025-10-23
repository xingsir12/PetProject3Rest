package ru.xing.springcourse.petproject3rest.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SensorIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("PetProject3rest")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SensorRepository sensorRepository;

    @BeforeEach
    void cleanUp(){
        sensorRepository.deleteAll();
    }

    @Test
    @Order(1)
    void shouldRegisterSensor() {
        // Given entity
        SensorDTO sensor = new SensorDTO("Sensor_Test", null);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("admin", "admin123");
        HttpEntity<SensorDTO> request = new HttpEntity<>(sensor, httpHeaders);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/sensors/register", request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify in database
        Optional<Sensor> saved = sensorRepository.findByName("Sensor_Test");
        assertThat(saved.isPresent()).isTrue();
    }

    @Test
    @Order(2)
    void shouldRejectDuplicateSensor() {
        // Given - sensor already exist
        Sensor existing = new Sensor();
        existing.setName("Sensor_Test");
        sensorRepository.save(existing);

        SensorDTO sensor = new SensorDTO("Sensor_Test", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");
        HttpEntity<SensorDTO> request = new HttpEntity<>(sensor, headers);

        // When trying to register duplicate
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/sensors/register", request, Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message"))
                .isEqualTo("Sensor already exists: Sensor_Test");

        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
    }

    @Test
    @Order(3)
    void shouldGetAllSensorsWithPagination() {
        // Given
        for(int i = 0; i < 25; i++){
            Sensor sensor = new Sensor();
            sensor.setName("Sensor_" + i);
            sensorRepository.save(sensor);
        }

        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/sensors?page=0&size=10", Map.class
        );

        //Assert
        System.out.println("Response: " + response.getBody());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = response.getBody();
        assertThat(body.get("totalElements")).isEqualTo(25);
        assertThat(body.get("totalPages")).isEqualTo(3);
        assertThat(((List) body.get("content")).size()).isEqualTo(10);
    }
}
