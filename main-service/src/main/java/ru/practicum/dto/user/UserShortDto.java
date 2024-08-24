package ru.practicum.dto.user;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {

    private Long id;

    private String name;

}