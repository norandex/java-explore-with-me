package ru.practicum.controller.adminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.user.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest newUserRequest) {
        return userService.create(newUserRequest);
    }

    @GetMapping
    public List<UserDto> get(@RequestParam(required = false, name = "ids") List<Long> ids,
                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return userService.get(ids, from, size);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
