package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid CreateBookingDto createBookingDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на добавление бронирования: {} пользователем: {}", createBookingDto.getItemId(), userId);
        createBookingDto.setUserId(userId);
        return bookingClient.createBooking(createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable Long bookingId, @RequestParam @NotNull boolean approved, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на обработку запроса на бронирование: {} пользователем: {}", bookingId, userId);
        return bookingClient.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение данных о конкретном бронировании: {}", bookingId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение списка всех бронирований пользователя: {}", userId);
        return bookingClient.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос на получение списка бронирований для всех вещей пользователя: {}", userId);
        return bookingClient.getBookingsForOwner(userId, state);
    }
}