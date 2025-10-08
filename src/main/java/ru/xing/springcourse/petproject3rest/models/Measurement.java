package ru.xing.springcourse.petproject3rest.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "measurement")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "sensor")
public class Measurement {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "value")
    private String value;

    @Column(name = "raining")
    private boolean raining;

    @Column(name = "measurement_date_time")
    private LocalDateTime measurementDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor")
    @JsonBackReference
    private Sensor sensor;
}
