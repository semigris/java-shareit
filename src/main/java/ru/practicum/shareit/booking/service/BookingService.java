package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SaveBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(SaveBookingDto createBookingDto);

    BookingDto update(Long bookingId, boolean approved, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookings(Long userId, String state);

    List<BookingDto> getBookingsForOwner(Long userId, String state);
}
