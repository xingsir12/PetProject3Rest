package ru.xing.springcourse.petproject3rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.xing.springcourse.petproject3rest.models.Sensor;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> findByName(String name);

    //Решение проблемы N+1 запросов
    @Query("SELECT s FROM Sensor s LEFT JOIN FETCH s.measurement")
    List<Sensor> findAllWithMeasurement();

}
