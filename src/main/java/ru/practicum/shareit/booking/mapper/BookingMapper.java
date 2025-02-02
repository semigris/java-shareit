package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SaveBookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "booker", target = "booker")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "status", constant = "WAITING")
    Booking toBooking(SaveBookingDto saveBookingDto);
}
