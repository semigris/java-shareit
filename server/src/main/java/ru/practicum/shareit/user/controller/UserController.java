package ru.practicum.shareit.user.controller;

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
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.debug("Запрос на создание пользователя: {}", userDto);
        return userService.create(userDto);
    }

    /**
     * Обновление данных пользователя по id
     */
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.debug("Запрос на обновление данных пользователя по id: {}", userId);
        return userService.update(userId, userDto);
    }

    /**
     * Получение информации о пользователе по id
     */
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("Запрос на получение информации о пользователе по id: {}", userId);
        return userService.getById(userId);
    }

    /**
     * Удаление пользователя по id
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Запрос на удаление пользователя по id: {}", userId);
        userService.delete(userId);
    }

    /**
     * Получение информации о всех пользователях
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Запрос на получение информации о всех пользователях");
        return userService.getAll();
    }
}
