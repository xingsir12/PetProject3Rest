package ru.xing.springcourse.petproject3rest.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class RoleListConverter implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return String.join(DELIMITER, roles);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(DELIMITER))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
