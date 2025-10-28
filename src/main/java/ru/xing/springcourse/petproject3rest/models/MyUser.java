package ru.xing.springcourse.petproject3rest.models;

import jakarta.persistence.*;
import lombok.*;
import ru.xing.springcourse.petproject3rest.util.RoleListConverter;

import javax.management.relation.RoleList;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "my_user")
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role", length = 255)
    @Convert(converter = RoleListConverter.class)
    private List<String> role = new ArrayList<>();
}
