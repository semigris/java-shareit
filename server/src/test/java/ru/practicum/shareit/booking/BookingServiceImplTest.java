package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @PersistenceContext
    private EntityManager em;

    @Test
    void shouldCreateBooking() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto booker = new UserDto();
        booker.setName("Another User Name");
        booker.setEmail("AnotherUser@mail.com");
        UserDto createdBooker = userService.create(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.create(bookingDto, createdBooker.getId());

        assertNotNull(createdBooking);
        assertEquals(createdItem.getId(), createdBooking.getItem().getId());
        assertEquals(createdBooker.getId(), createdBooking.getBooker().getId());
    }

    @Test
    void shouldFailCreateBookingIfItemNotFound() {
        CreateBookingDto bookingDto = new CreateBookingDto(999L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, 1L));
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void shouldFailCreateBookingWhenItemIsUnavailable() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto booker = new UserDto();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        UserDto createdBooker = userService.create(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(false);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Exception exception = assertThrows(RuntimeException.class, () -> bookingService.create(bookingDto, createdBooker.getId()));
        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void shouldFailCreateBookingIfUserNotFound() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        UserDto createdOwner = userService.create(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, 999L));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void shouldFailCreateBookingOwnItem() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        UserDto createdOwner = userService.create(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Exception exception = assertThrows(RuntimeException.class, () -> bookingService.create(bookingDto, createdOwner.getId()));
        assertEquals("Нельзя забронировать свою же вещь", exception.getMessage());
    }

    @Test
    void shouldUpdatedStatusBooking() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto booker = new UserDto();
        booker.setName("Another User Name");
        booker.setEmail("AnotherUser@mail.com");
        UserDto createdBooker = userService.create(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto, createdBooker.getId());

        BookingDto updatedBooking = bookingService.update(createdBooking.getId(), true, createdOwner.getId());

        assertEquals(Status.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void shouldFailUpdatedStatusBookingIfBookingNotFound() {
        Exception exception = assertThrows(RuntimeException.class, () -> bookingService.update(88L, true, 1L));
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void shouldFailUpdateStatusBookingIfNotOwner() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto booker = new UserDto();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        UserDto createdBooker = userService.create(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto, createdBooker.getId());

        Exception exception = assertThrows(RuntimeException.class, () -> bookingService.update(createdBooking.getId(), true, createdBooker.getId()));
        assertEquals("Только владелец вещи может изменить статус бронирования", exception.getMessage());
    }

    @Test
    void shouldFailUpdateStatusIfBookingAlreadyApproved() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto booker = new UserDto();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        UserDto createdBooker = userService.create(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto, createdBooker.getId());
        bookingService.update(createdBooking.getId(), true, createdOwner.getId());

        Exception exception = assertThrows(RuntimeException.class, () -> bookingService.update(createdBooking.getId(), false, createdOwner.getId()));
        assertEquals("Нельзя изменить статус бронирования, если оно уже обработано", exception.getMessage());
    }


    @Test
    void shouldGetBooking() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto booker = new UserDto();
        booker.setName("Another User Name");
        booker.setEmail("AnotherUser@mail.com");
        UserDto createdBooker = userService.create(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto, createdBooker.getId());

        BookingDto foundBooking = bookingService.getBookingById(createdBooking.getId(), createdBooker.getId());

        assertNotNull(foundBooking);
        assertEquals(createdBooking.getId(), foundBooking.getId());
    }

    @Test
    void shouldFailGetBookingIfInvalidId() {
        Exception exception = assertThrows(RuntimeException.class, () -> bookingService.getBookingById(999L, 1L));
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void shouldFailGetBookingIfUserNotBookerAndNotOwner() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto booker = new UserDto();
        booker.setName("Another User Name");
        booker.setEmail("AnotherUser@mail.com");
        UserDto createdBooker = userService.create(booker);

        UserDto notBookerNotOwner = new UserDto();
        notBookerNotOwner.setName("Another User Name2");
        notBookerNotOwner.setEmail("AnotherUser2@mail.com");
        UserDto createdNotBookerNotOwner = userService.create(notBookerNotOwner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto, createdBooker.getId());

        Exception exception = assertThrows(UnavailableDataException.class, () -> bookingService.getBookingById(createdBooking.getId(), createdNotBookerNotOwner.getId()));
        assertEquals("Доступ к бронированию запрещён", exception.getMessage());
    }

    @Test
    void shouldGetAllBookings() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto booker = new UserDto();
        booker.setName("Another User Name");
        booker.setEmail("AnotherUser@mail.com");
        UserDto createdBooker = userService.create(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingService.create(bookingDto, createdBooker.getId());
        List<BookingDto> bookings = bookingService.getAllBookings(createdBooker.getId(), State.WAITING);

        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);

        bookings = bookingService.getAllBookings(createdBooker.getId(), State.PAST);

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void shouldGetAllBookingsEmptyList() {
        UserDto booker = new UserDto();
        booker.setName("Another User Name");
        booker.setEmail("AnotherUser@mail.com");
        UserDto createdBooker = userService.create(booker);

        List<BookingDto> bookings = bookingService.getAllBookings(createdBooker.getId(), State.ALL);

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void shouldFailGetAllBookingsWithInvalidUser() {
        Exception exception = assertThrows(RuntimeException.class, () -> bookingService.getAllBookings(999L, State.ALL));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void shouldFailGetBookingsForOwner() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto booker = new UserDto();
        booker.setName("Another User Name");
        booker.setEmail("AnotherUser@mail.com");
        UserDto createdBooker = userService.create(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        ItemDto anotherItemDto = new ItemDto();
        anotherItemDto.setName("Another Item");
        anotherItemDto.setDescription("Another Item Description");
        anotherItemDto.setAvailable(true);
        ItemDto createdAnotherItemDto = itemService.create(anotherItemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingService.create(bookingDto, createdBooker.getId());
        List<BookingDto> bookings = bookingService.getBookingsForOwner(createdOwner.getId(), State.WAITING);

        assertEquals(bookings.size(), 1);

        CreateBookingDto anotherBookingDto = new CreateBookingDto(createdAnotherItemDto.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingService.create(anotherBookingDto, createdBooker.getId());

        bookings = bookingService.getBookingsForOwner(createdOwner.getId(), State.WAITING);

        assertEquals(bookings.size(), 2);
    }

    @Test
    void shouldFailGetBookingsForOwnerWithInvalidUser() {
        Exception exception = assertThrows(RuntimeException.class, () -> bookingService.getBookingsForOwner(999L, State.ALL));
        assertEquals("Пользователь не найден", exception.getMessage());
    }
}

