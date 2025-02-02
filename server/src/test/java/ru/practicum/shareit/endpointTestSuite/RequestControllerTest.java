package ru.practicum.shareit.endpointTestSuite;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateRequest() throws Exception {
        CreateRequestDto createRequestDto = new CreateRequestDto();
        createRequestDto.setUserId(2L);
        createRequestDto.setDescription("Request Description");

        RequestDto requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Request Description");

        when(requestService.create(any(CreateRequestDto.class))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldGetRequest() throws Exception {
        Long requestId = 1L;
        Long userId = 2L;
        RequestDto requestDto = new RequestDto();
        requestDto.setId(requestId);
        requestDto.setDescription("Request Description");

        when(requestService.getRequestById(eq(requestId), eq(userId))).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Request Description"));
    }

    @Test
    void shouldGetOwnRequests() throws Exception {
        Long userId = 2L;
        List<RequestDto> requests = List.of(new RequestDto());

        when(requestService.getOwnRequests(eq(userId))).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        Long userId = 2L;
        List<RequestDto> requests = List.of(new RequestDto());

        when(requestService.getAllRequests(eq(userId), eq(0), eq(10))).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

