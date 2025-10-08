package ru.xing.springcourse.petproject3rest.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Builder
public class Sensor {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "sensor" ,cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Measurement> measurement;
}
