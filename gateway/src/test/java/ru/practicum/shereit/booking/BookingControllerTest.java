package ru.practicum.shereit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@ContextConfiguration(classes = ShareItGateway.class)
class BookingControllerTest {

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldCreateBooking() {
        Long userId = 1L;
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(1L);
        createBookingDto.setStart(LocalDateTime.now().plusDays(1));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(new ItemDto());
        bookingDto.setBooker(new UserDto());

        when(bookingClient.createBooking(eq(userId), any(CreateBookingDto.class))).thenReturn(ResponseEntity.ok(bookingDto));

        ResponseEntity<Object> response = bookingController.createBooking(createBookingDto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldUpdatedBooking() {
        Long bookingId = 1L;
        Long userId = 2L;
        boolean approved = true;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStatus("APPROVED");

        when(bookingClient.updateBooking(eq(bookingId), eq(approved), eq(userId))).thenReturn(ResponseEntity.ok(bookingDto));

        ResponseEntity<Object> response = bookingController.updateBooking(bookingId, approved, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGetBooking() {
        Long bookingId = 1L;
        Long userId = 2L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);

        when(bookingClient.getBooking(eq(bookingId), eq(userId))).thenReturn(ResponseEntity.ok(bookingDto));

        ResponseEntity<Object> response = bookingController.getBooking(bookingId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGetAllBookings() {
        Long userId = 2L;
        State state = State.ALL;
        List<BookingDto> bookings = List.of(new BookingDto());

        when(bookingClient.getAllBookings(eq(userId), eq(state))).thenReturn(ResponseEntity.ok(bookings));

        ResponseEntity<Object> response = bookingController.getAllBookings(state, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGetBookingsForOwner() {
        Long userId = 2L;
        State state = State.ALL;
        List<BookingDto> bookings = List.of(new BookingDto());

        when(bookingClient.getBookingsForOwner(eq(userId), eq(state))).thenReturn(ResponseEntity.ok(bookings));

        ResponseEntity<Object> response = bookingController.getBookingsForOwner(state, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

