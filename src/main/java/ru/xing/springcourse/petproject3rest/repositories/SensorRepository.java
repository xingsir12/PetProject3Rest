package ru.xing.springcourse.petproject3rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.xing.springcourse.petproject3rest.models.Sensor;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> findByName(String name);
}
