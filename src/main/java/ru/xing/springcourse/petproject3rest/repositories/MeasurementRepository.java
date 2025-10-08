package ru.xing.springcourse.petproject3rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.xing.springcourse.petproject3rest.models.Measurement;

public interface MeasurementRepository extends JpaRepository<Measurement, Integer> {
}
