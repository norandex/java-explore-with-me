package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.model.enums.RequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "requester")
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus requestStatus;

}
