package ru.practicum.shareit.booking.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableDataException;
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
    @Transactional
    public BookingDto create(CreateBookingDto createBookingDto, Long userId) {
        log.debug("Обработка запроса на добавление бронирования: {}", createBookingDto);

        Item item = itemRepository.findById(createBookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getAvailable()) {
            throw new UnavailableDataException("Вещь недоступна для бронирования");
        }

        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new UnavailableDataException("Нельзя забронировать свою же вещь");
        }

        Booking booking = bookingMapper.toBooking(createBookingDto, item, booker);
        booking.setItem(item);
        booking.setBooker(booker);
        bookingRepository.save(booking);
        BookingDto createdBooking = bookingMapper.toBookingDto(booking);

        log.debug("Бронирование успешно создано: {}", createdBooking);
        return createdBooking;
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, boolean approved, Long userId) {
        log.debug("Обработка запроса на обновление статуса бронирования с {} на {}", bookingId, approved);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UnavailableDataException("Только владелец вещи может изменить статус бронирования");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new UnavailableDataException("Нельзя изменить статус бронирования, если оно уже обработано");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        BookingDto updatedBooking = bookingMapper.toBookingDto(booking);

        log.debug("Статус бронирования обновлён: {}", updatedBooking);
        return updatedBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.debug("Обработка запроса на получение данных о конкретном бронировании: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new UnavailableDataException("Доступ к бронированию запрещён");
        }

        BookingDto foundBooking = bookingMapper.toBookingDto(booking);

        log.debug("Получено бронирование: {}", foundBooking);
        return foundBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings(Long userId, State state) {
        log.debug("Обработка запроса на получение списка всех бронирований пользователя: {} со статусом {}", userId, state);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentBookingsByBookerId(userId);
            case PAST -> bookingRepository.findPastBookingsByBookerId(userId);
            case FUTURE -> bookingRepository.findFutureBookingsByBookerId(userId);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };

        return bookings.stream().map(bookingMapper::toBookingDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsForOwner(Long userId, State state) {
        log.debug("Обработка запроса на получение списка бронирований для всех вещей пользователя: {} со статусом {}", userId, state);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentBookingsByOwnerId(userId);
            case PAST -> bookingRepository.findPastBookingsByOwnerId(userId);
            case FUTURE -> bookingRepository.findFutureBookingsByOwnerId(userId);
            case WAITING -> bookingRepository.findByOwnerIdAndStatus(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findByOwnerIdAndStatus(userId, Status.REJECTED);
            default -> bookingRepository.findByOwnerIdOrderByStartDesc(userId);
        };

        return bookings.stream().map(bookingMapper::toBookingDto).toList();
    }
}
