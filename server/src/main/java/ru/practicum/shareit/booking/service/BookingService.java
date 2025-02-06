package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {
    BookingDto create(CreateBookingDto createBookingDto, Long userId);

    BookingDto update(Long bookingId, boolean approved, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookings(Long userId, State state);

    List<BookingDto> getBookingsForOwner(Long userId, State state);
}
