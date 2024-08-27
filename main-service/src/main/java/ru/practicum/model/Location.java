package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "locations")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float lat;

    private Float lon;
}