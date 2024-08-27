package ru.practicum.dto.like;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

    private String userName;

    private String eventTitle;

    private Boolean isLike;

    private String createdOn;

}
