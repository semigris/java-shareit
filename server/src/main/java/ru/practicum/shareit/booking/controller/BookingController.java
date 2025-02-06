package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    /**
     * Добавление бронирования
     */
    @PostMapping
    public BookingDto createBooking(@RequestBody CreateBookingDto createBookingDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на добавление бронирования: {} пользователем: {}", createBookingDto.getItemId(), userId);
        return bookingService.create(createBookingDto, userId);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование
     */
    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId, @RequestParam boolean approved, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на обработку запроса на бронирование: {} пользователем: {}", bookingId, userId);
        return bookingService.update(bookingId, approved, userId);
    }

    /**
     * Получение данных о конкретном бронировании
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение данных о конкретном бронировании: {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    /**
     * Получение списка всех бронирований текущего пользователя
     */
    @GetMapping
    public List<BookingDto> getAllBookings(@RequestParam(defaultValue = "ALL") State state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение списка всех бронирований пользователя: {}", userId);
        return bookingService.getAllBookings(userId, state);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя
     */
    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwner(@RequestParam(defaultValue = "ALL") State state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение списка бронирований для всех вещей пользователя: {}", userId);
        return bookingService.getBookingsForOwner(userId, state);
    }
}
