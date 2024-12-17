package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    /**
     * Добавление вещи
     */
    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Добавление вещи " + itemDto.getName() + " пользователем " + userId);
        return itemService.create(itemDto, userId);
    }

    /**
     * Обновление информации о вещи
     */
    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @PathVariable Long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Обновление информации о вещи с id " + itemId);
        return itemService.update(itemId, itemDto, userId);
    }

    /**
     * Получение информации о вещи
     */
    @GetMapping("/{itemId}")
    public ItemDto getItem(@Valid @PathVariable Long itemId) {
        log.debug("Получение информации о вещи по id " + itemId);
        return itemService.getItemById(itemId);
    }

    /**
     * Получение информации о всех вещах пользователя
     */
    @GetMapping
    public List<ItemDto> getAll(@Valid @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение информации о всех вещах пользователя с id " + userId);
        return itemService.getAllItems(userId);
    }

    /**
     * Поиск вещи
     */
    @GetMapping("/search")
    public List<ItemDto> search(@Valid @RequestParam String text) {
        log.debug("Поиск вещи " + text);
        return itemService.searchItems(text);
    }
}
