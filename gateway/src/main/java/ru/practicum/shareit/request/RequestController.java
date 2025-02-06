package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody CreateRequestDto createRequestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на создание запроса на вещь пользователем: {}", createRequestDto.getUserId());
        createRequestDto.setUserId(userId);
        return requestClient.createRequest(createRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение данных о запросе: {} пользователем: {}", requestId, userId);
        return requestClient.getRequest(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение списка своих запросов пользователем: {}", userId);
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        log.debug("Запрос на получение списка запросов других пользователей пользователем: {}", userId);
        return requestClient.getAllRequests(userId, from, size);
    }
}
