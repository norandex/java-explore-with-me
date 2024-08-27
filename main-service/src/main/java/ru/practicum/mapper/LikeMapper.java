package ru.practicum.mapper;

import ru.practicum.dto.like.LikeDto;
import ru.practicum.model.Like;

public class LikeMapper {
    public static LikeDto toLikeDto(Like like) {
        return LikeDto.builder()
                .eventTitle(like.getId().getEvent().getTitle())
                .userName(like.getId().getUser().getName())
                .isLike(like.getIsLike())
                .createdOn(DateMapper.formatToString(like.getCreatedOn()))
                .build();
    }
}
