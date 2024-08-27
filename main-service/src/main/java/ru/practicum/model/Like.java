package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Like {

    @EmbeddedId
    private LikeId id;

    @Column(name = "is_like")
    @NotNull
    private Boolean isLike;

    @Column(name = "created_on")
    @NotNull
    private LocalDateTime createdOn;


}
