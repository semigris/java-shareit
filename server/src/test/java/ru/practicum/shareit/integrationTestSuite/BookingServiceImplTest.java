package ru.practicum.shareit.integrationTestSuite;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
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

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(), createdBooker.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.create(bookingDto);

        assertNotNull(createdBooking);
        assertEquals(createdItem.getId(), createdBooking.getItem().getId());
        assertEquals(createdBooker.getId(), createdBooking.getBooker().getId());
    }

    @Test
    void shouldCreateBookingIfItemNotFound() {
        CreateBookingDto bookingDto = new CreateBookingDto(999L, 1L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto));
    }

    @Test
    void shouldUpdatedBooking() {
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

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(), createdBooker.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto);

        BookingDto updatedBooking = bookingService.update(createdBooking.getId(), true, createdOwner.getId());

        assertEquals(Booking.Status.APPROVED.toString(), updatedBooking.getStatus());
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

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(), createdBooker.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto);

        BookingDto foundBooking = bookingService.getBookingById(createdBooking.getId(), createdBooker.getId());

        assertNotNull(foundBooking);
        assertEquals(createdBooking.getId(), foundBooking.getId());
    }

    @Test
    void shouldGetAllBookings() {
        UserDto booker = new UserDto();
        booker.setName("Another User Name");
        booker.setEmail("AnotherUser@mail.com");
        UserDto createdBooker = userService.create(booker);

        List<BookingDto> bookings = bookingService.getAllBookings(createdBooker.getId(), "ALL");

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }
}

