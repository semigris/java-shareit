package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(CreateBookingDto createBookingDto) {
        return post("", createBookingDto.getUserId(), createBookingDto);
    }

    public ResponseEntity<Object> updateBooking(long bookingId, boolean approved, long userId) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBooking(Long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookings(Long userId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingsForOwner(Long userId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get("/owner?state={state}", userId, parameters);
    }
}