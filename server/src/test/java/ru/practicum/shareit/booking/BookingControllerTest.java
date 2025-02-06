package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateBooking() throws Exception {
        Long userId = 2L;
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(1L);
        createBookingDto.setStart(LocalDateTime.now().plusDays(1));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(new ItemDto());
        bookingDto.setBooker(new UserDto());

        when(bookingService.create(any(CreateBookingDto.class), eq(userId))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldUpdateBooking() throws Exception {
        Long bookingId = 1L;
        Long userId = 2L;
        boolean approved = true;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStatus(Status.APPROVED);

        when(bookingService.update(eq(bookingId), eq(approved), eq(userId))).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldGetBooking() throws Exception {
        Long bookingId = 1L;
        Long userId = 2L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);

        when(bookingService.getBookingById(eq(bookingId), eq(userId))).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldGetAllBookings() throws Exception {
        Long userId = 2L;
        State state = State.ALL;
        List<BookingDto> bookings = List.of(new BookingDto());

        when(bookingService.getAllBookings(eq(userId), eq(state))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .param("state", String.valueOf(state))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetBookingsForOwner() throws Exception {
        Long userId = 2L;
        State state = State.ALL;
        List<BookingDto> bookings = List.of(new BookingDto());

        when(bookingService.getBookingsForOwner(eq(userId), eq(state))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", String.valueOf(state))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
