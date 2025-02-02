package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    /**
     * Добавление запроса на вещь
     */
    @PostMapping
    public RequestDto createRequest(@RequestBody CreateRequestDto createRequestDto) {
        log.debug("Запрос на создание запроса на вещь пользователем: {}", createRequestDto.getUserId());
        return requestService.create(createRequestDto);
    }

    /**
     * Получение данных об одном запросе
     */
    @GetMapping("/{requestId}")
    public RequestDto getRequest(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение данных о запросе: {} пользователем: {}", requestId, userId);
        return requestService.getRequestById(requestId, userId);
    }

    /**
     * Получение списка своих запросов
     */
    @GetMapping
    public List<RequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение списка своих запросов пользователем: {}", userId);
        return requestService.getOwnRequests(userId);
    }

    /**
     * Получение списка запросов других пользователей
     */
    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        log.debug("Запрос на получение списка запросов других пользователей пользователем: {}", userId);
        return requestService.getAllRequests(userId, from, size);
    }
}
