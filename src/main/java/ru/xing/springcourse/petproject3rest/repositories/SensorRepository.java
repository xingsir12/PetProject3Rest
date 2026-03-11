package ru.xing.springcourse.petproject3rest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.xing.springcourse.petproject3rest.models.Sensor;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> findByName(String name);

    boolean existsByName(String name);

    // Для пагинированного списка с измерениями
    Page<Sensor> findAll(Pageable pageable);

    // Для поиска по имени с измерениями
    @Query("SELECT s FROM Sensor s LEFT JOIN FETCH s.measurements WHERE s.name = :name")
    Optional<Sensor> findWithMeasurementsByName(@Param("name") String name);

}
