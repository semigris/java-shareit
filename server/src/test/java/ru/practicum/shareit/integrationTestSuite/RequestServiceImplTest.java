package ru.practicum.shareit.integrationTestSuite;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTest {

    @Autowired
    private RequestService requestService;
    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager em;

    @Test
    void shouldCreateRequest() {
        UserDto user = new UserDto();
        user.setName("User Name");
        user.setEmail("User@mail.com");
        UserDto createdUser = userService.create(user);

        CreateRequestDto requestDto = new CreateRequestDto();
        requestDto.setUserId(createdUser.getId());
        requestDto.setDescription("Request Description");

        RequestDto createdRequest = requestService.create(requestDto);

        assertNotNull(createdRequest);
        assertEquals("Request Description", createdRequest.getDescription());
    }

    @Test
    void shouldCreateRequestIfUserNotFound() {
        CreateRequestDto requestDto = new CreateRequestDto();
        requestDto.setUserId(999L);
        requestDto.setDescription("Request Description");

        assertThrows(NotFoundException.class, () -> requestService.create(requestDto));
    }

    @Test
    void shouldGetRequest() {
        UserDto user = new UserDto();
        user.setName("User Name");
        user.setEmail("User@mail.com");
        UserDto createdUser = userService.create(user);

        CreateRequestDto requestDto = new CreateRequestDto();
        requestDto.setUserId(createdUser.getId());
        requestDto.setDescription("Request Description");
        RequestDto createdRequest = requestService.create(requestDto);

        RequestDto foundRequest = requestService.getRequestById(createdRequest.getId(), createdUser.getId());

        assertNotNull(foundRequest);
        assertEquals("Request Description", foundRequest.getDescription());
    }

    @Test
    void shouldGetRequestIfRequestNotFound() {
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(999L, 1L));
    }

    @Test
    void shouldGetOwnRequests() {
        UserDto user = new UserDto();
        user.setName("User Name");
        user.setEmail("User@mail.com");
        UserDto createdUser = userService.create(user);

        CreateRequestDto requestDto1 = new CreateRequestDto();
        requestDto1.setUserId(createdUser.getId());
        requestDto1.setDescription("Request Description");
        requestService.create(requestDto1);

        CreateRequestDto requestDto2 = new CreateRequestDto();
        requestDto2.setUserId(createdUser.getId());
        requestDto2.setDescription("Another Request Description");
        requestService.create(requestDto2);

        List<RequestDto> requests = requestService.getOwnRequests(createdUser.getId());

        assertEquals(2, requests.size());
    }

    @Test
    void shouldGetAllRequests() {
        UserDto user1 = new UserDto();
        user1.setName("User");
        user1.setEmail("User@mail.com");
        UserDto createdUser1 = userService.create(user1);

        UserDto user2 = new UserDto();
        user2.setName("Another User");
        user2.setEmail("AnotherUser@mail.com");
        UserDto createdUser2 = userService.create(user2);

        CreateRequestDto requestDto = new CreateRequestDto();
        requestDto.setUserId(createdUser1.getId());
        requestDto.setDescription("Request Description");
        requestService.create(requestDto);

        List<RequestDto> requests = requestService.getAllRequests(createdUser2.getId(), 0, 10);

        assertEquals(1, requests.size());
        assertEquals("Request Description", requests.get(0).getDescription());
    }
}
