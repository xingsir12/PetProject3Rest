package ru.xing.springcourse.petproject3rest.integration;

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
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecurityIntegrationTest {
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

    @Test
    void shouldReject_WhenNoAuth() {
        // When - try to register sensor without auth
        SensorDTO sensor = new SensorDTO("Test", null);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/sensor/register", sensor, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReject_WhenUserTriesToRegisterSensor() {
        // When - USER tries to register sensor (only ADMIN_ROLE)
        SensorDTO sensor = new SensorDTO("Test", null);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "user123");
        HttpEntity<SensorDTO> request = new HttpEntity<>(sensor, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/sensors/register", request, String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldAllow_PublicEndpoints() {
        // When - публичный endpoint БЕЗ авторизации
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/sensors?page=0&size=10", Map.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
