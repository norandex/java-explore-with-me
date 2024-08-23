package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {

    private String app;
    private String uri;
    private Long hits;
}
