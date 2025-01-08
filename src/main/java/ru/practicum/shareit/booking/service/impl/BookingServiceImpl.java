package ru.practicum.shareit.booking.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SaveBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(SaveBookingDto saveBookingDto) {
        log.debug("Создание бронирования {}", saveBookingDto);

        Item item = itemRepository.findById(saveBookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getAvailable()) {
            throw new RuntimeException("Вещь недоступна для бронирования");
        }

        User booker = userRepository.findById(saveBookingDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new RuntimeException("Нельзя забронировать свою же вещь");
        }

        Booking booking = bookingMapper.toBooking(saveBookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        bookingRepository.save(booking);
        BookingDto savedBooking = bookingMapper.toBookingDto(booking);

        log.debug("Бронирование успешно создано: {}", savedBooking);
        return savedBooking;
    }

    @Override
    public BookingDto update(Long bookingId, boolean approved, Long userId) {
        log.debug("Обновление статуса бронирования с {} на {}", bookingId, approved);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Только владелец вещи может изменить статус бронирования");
        }

        if (!booking.getStatus().equals(Booking.Status.WAITING)) {
            throw new RuntimeException("Нельзя изменить статус бронирования, если оно уже обработано");
        }

        booking.setStatus(approved ? Booking.Status.APPROVED : Booking.Status.REJECTED);
        bookingRepository.save(booking);
        BookingDto updatedBooking = bookingMapper.toBookingDto(booking);

        log.debug("Статус бронирования обновлён: {}", updatedBooking);
        return updatedBooking;
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.debug("Получение бронирования {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
            !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotValidException("Доступ к бронированию запрещён");
        }

        BookingDto foundBooking = bookingMapper.toBookingDto(booking);

        log.debug("Получено бронирование: {}", foundBooking);
        return foundBooking;
    }

    @Override
    public List<BookingDto> getAllBookings(Long userId, String state) {
        log.debug("Получение всех бронирований для пользователя {} со статусом {}", userId, state);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookingsByBookerId(userId);
                break;
            case "PAST":
                bookings = bookingRepository.findPastBookingsByBookerId(userId);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureBookingsByBookerId(userId);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Booking.Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Booking.Status.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Long userId, String state) {
        log.debug("Получение бронирований вещей для пользователя {} со статусом {}", userId, state);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookingsByOwnerId(userId);
                break;
            case "PAST":
                bookings = bookingRepository.findPastBookingsByOwnerId(userId);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureBookingsByOwnerId(userId);
                break;
            case "WAITING":
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, Booking.Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, Booking.Status.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByOwnerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }
}
