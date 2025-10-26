package ru.xing.springcourse.petproject3rest.unit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.xing.springcourse.petproject3rest.util.RoleListConverter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoleListConverterTest {
    private RoleListConverter converter;

    @BeforeEach
    void setUp() {
        converter = new RoleListConverter();
    }

    @Test
    void convertToDatabaseColumn_SingleRole() {
        List<String> roles = List.of("ROLE_USER");

        String result = converter.convertToDatabaseColumn(roles);

        assertEquals("ROLE_USER", result);
    }

    @Test
    void convertToDatabaseColumn_MultipleRoles() {
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

        String result = converter.convertToDatabaseColumn(roles);

        assertEquals("ROLE_USER,ROLE_ADMIN", result);
    }

    @Test
    void convertToDatabaseColumn_EmptyList() {
        List<String> roles = List.of();

        String result = converter.convertToDatabaseColumn(roles);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void convertToDatabaseColumn_NullList() {
        String result = converter.convertToDatabaseColumn(null);

        assertNull(result);
    }

    @Test
    void convertToEntityAttribute_SingleRole() {
        String dbData = "ROLE_USER";

        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ROLE_USER", result.get(0));
    }

    @Test
    void convertToEntityAttribute_MultipleRoles() {
        String dbData = "ROLE_USER,ROLE_ADMIN";

        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("ROLE_USER"));
        assertTrue(result.contains("ROLE_ADMIN"));
    }

    @Test
    void convertToEntityAttribute_WithSpaces() {
        String dbData = "ROLE_USER , ROLE_ADMIN";

        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(r -> r.contains(" ")));
    }

    @Test
    void convertToEntityAttribute_EmptyString() {
        String dbData = "";

        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertToEntityAttribute_NullString() {
        List<String> result = converter.convertToEntityAttribute(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void roundTrip_PreservesData() {
        List<String> original = List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR");

        String dbData = converter.convertToDatabaseColumn(original);
        List<String> restored = converter.convertToEntityAttribute(dbData);

        assertEquals(original.size(), restored.size());
        assertTrue(restored.containsAll(original));
    }
}
