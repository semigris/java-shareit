package ru.practicum.shereit.endpointTestSuite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@ContextConfiguration(classes = UserController.class)
class UserControllerTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User Name");

        when(userClient.createUser(any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        ResponseEntity<Object> response = userController.createUser(userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldUpdateUser() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("User Name Updated");

        when(userClient.updateUser(eq(userId), any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        ResponseEntity<Object> response = userController.updateUser(userId, userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGetUser() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);

        when(userClient.getUser(eq(userId))).thenReturn(ResponseEntity.ok(userDto));

        ResponseEntity<Object> response = userController.getUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldDeleteUser() {
        Long userId = 1L;

        when(userClient.deleteUser(eq(userId))).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<Object> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGetAllUsers() {
        List<UserDto> users = List.of(new UserDto());

        when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok(users));

        ResponseEntity<Object> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

