package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SaveBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    /**
     * Добавление бронирования
     */
    @PostMapping
    public BookingDto saveBooking(@RequestBody @Valid SaveBookingDto saveBookingDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на добавление бронирования вещи: {} пользователем: {}", saveBookingDto.getItemId(), userId);
        saveBookingDto.setUserId(userId);
        return bookingService.create(saveBookingDto);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование
     */
    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId, @RequestParam boolean approved, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Обработка запроса на бронирование: {} пользователем: {}", bookingId, userId);
        return bookingService.update(bookingId, approved, userId);
    }

    /**
     * Получение данных о конкретном бронировании
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение данных о конкретном бронировании: {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    /**
     * Получение списка всех бронирований текущего пользователя
     */
    @GetMapping
    public List<BookingDto> getAll(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение списка всех бронирований пользователя: {}", userId);
        return bookingService.getAllBookings(userId, state);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя
     */
    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwner(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение списка бронирований для всех вещей пользователя: {}", userId);
        return bookingService.getBookingsForOwner(userId, state);
    }
}
