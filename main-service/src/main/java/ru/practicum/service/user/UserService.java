package ru.practicum.service.user;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest newUserRequest);

    void delete(Long userId);

    List<UserDto> get(List<Long> userIds, Integer from, Integer size);
}