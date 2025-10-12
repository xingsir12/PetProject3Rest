package ru.xing.springcourse.petproject3rest.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "sensor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "measurement")
public class Sensor {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "sensor" ,cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Measurement> measurement;
}
