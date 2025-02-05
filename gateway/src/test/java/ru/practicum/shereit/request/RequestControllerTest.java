package ru.practicum.shereit.request;

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
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@ContextConfiguration(classes = ShareItGateway.class)
class RequestControllerTest {

    @Mock
    private RequestClient requestClient;

    @InjectMocks
    private RequestController requestController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldCreateRequest() {
        Long userId = 1L;
        CreateRequestDto createRequestDto = new CreateRequestDto();
        createRequestDto.setDescription("Request description");

        RequestDto requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setRequestor(new UserDto());

        when(requestClient.createRequest(any(CreateRequestDto.class))).thenReturn(ResponseEntity.ok(requestDto));

        ResponseEntity<Object> response = requestController.createRequest(createRequestDto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGetRequest() {
        Long requestId = 1L;
        Long userId = 2L;
        RequestDto requestDto = new RequestDto();
        requestDto.setId(requestId);

        when(requestClient.getRequest(eq(requestId), eq(userId))).thenReturn(ResponseEntity.ok(requestDto));

        ResponseEntity<Object> response = requestController.getRequest(requestId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGetOwnRequests() {
        Long userId = 2L;
        List<RequestDto> requests = List.of(new RequestDto());

        when(requestClient.getOwnRequests(eq(userId))).thenReturn(ResponseEntity.ok(requests));

        ResponseEntity<Object> response = requestController.getOwnRequests(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGetAllRequests() {
        Long userId = 2L;
        int from = 0;
        int size = 10;
        List<RequestDto> requests = List.of(new RequestDto());

        when(requestClient.getAllRequests(eq(userId), eq(from), eq(size))).thenReturn(ResponseEntity.ok(requests));

        ResponseEntity<Object> response = requestController.getAllRequests(userId, from, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

