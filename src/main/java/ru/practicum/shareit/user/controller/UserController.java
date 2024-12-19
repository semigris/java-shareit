package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    /**
     * Создание пользователя
     */
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Запрос на создание пользователя: {}", userDto);
        return userService.save(userDto);
    }

    /**
     * Получение информации о всех пользователях
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Запрос на получение информации о всех пользователях");
        return userService.getAll();
    }

    /**
     * Получение информации о пользователе по id
     */
    @GetMapping("/{id}")
    public UserDto getUserById(@Valid @PathVariable Long id) {
        log.debug("Запрос на получение информации о пользователе по id: {}", id);
        return userService.getById(id);
    }

    /**
     * Обновление данных пользователя по id
     */
    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @PathVariable Long id, @RequestBody UserDto userDto) {
        log.debug("Запрос на обновление данных пользователя по id: {}", id);
        return userService.update(id, userDto);
    }

    /**
     * Удаление пользователя по id
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@Valid @PathVariable Long id) {
        log.debug("Запрос на удаление пользователя по id: {}", id);
        userService.delete(id);
    }
}
