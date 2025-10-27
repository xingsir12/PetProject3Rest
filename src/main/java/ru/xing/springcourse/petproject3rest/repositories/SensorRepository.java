package ru.xing.springcourse.petproject3rest.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.xing.springcourse.petproject3rest.models.Sensor;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> findByName(String name);

    boolean existsByName(String name);

    // Для пагинированного списка с измерениями
    @EntityGraph(attributePaths = {"measurements"})
    Page<Sensor> findAll(Pageable pageable);

    // Для поиска по имени с измерениями
    @EntityGraph(attributePaths = {"measurements"})
    Optional<Sensor> findWithMeasurementsByName(String name);

}
