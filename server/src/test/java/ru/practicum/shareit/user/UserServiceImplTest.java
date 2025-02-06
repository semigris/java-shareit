package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void shouldCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("User Name");
        userDto.setEmail("User@mail.com");

        UserDto createdUser = userService.create(userDto);

        assertNotNull(createdUser);
        assertEquals("User Name", createdUser.getName());
        assertEquals("User@mail.com", createdUser.getEmail());

        User user = em.find(User.class, createdUser.getId());
        assertNotNull(user);
    }

    @Test
    void shouldCreateUserIfEmailExists() {
        UserDto userDto = new UserDto();
        userDto.setName("Another User Name");
        userDto.setEmail("User@mail.com");

        userService.create(userDto);

        assertThrows(UnavailableDataException.class, () -> userService.create(userDto));
    }

    @Test
    void shouldUpdateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("User Name");
        userDto.setEmail("aaaaaa@mail.com");

        UserDto createdUser = userService.create(userDto);
        createdUser.setName("Updated Name");
        createdUser.setEmail("bbbbb@mail.com");

        UserDto updatedUser = userService.update(createdUser.getId(), createdUser);

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("bbbbb@mail.com", updatedUser.getEmail());

        User updatedEntity = em.find(User.class, createdUser.getId());
        assertNotNull(updatedEntity);
        assertEquals("Updated Name", updatedEntity.getName());
    }

    @Test
    void shouldUpdateUserIfUserNotFound() {
        UserDto userDto = new UserDto();
        userDto.setName("User Name");
        userDto.setEmail("nonexistent@mail.com");

        assertThrows(NotFoundException.class, () -> userService.update(999L, userDto));
    }

    @Test
    void shouldGetUser() {
        UserDto userDto = new UserDto();
        userDto.setName("User Name");
        userDto.setEmail("User@mail.com");

        UserDto createdUser = userService.create(userDto);

        UserDto foundUser = userService.getById(createdUser.getId());

        assertNotNull(foundUser);
        assertEquals("User Name", foundUser.getName());
    }

    @Test
    void shouldGetUserIfUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getById(999L));
    }

    @Test
    void shouldDeleteUser() {
        UserDto userDto = new UserDto();
        userDto.setName("User Name");
        userDto.setEmail("User@mail.com");

        UserDto createdUser = userService.create(userDto);

        userService.delete(createdUser.getId());

        assertThrows(NotFoundException.class, () -> userService.getById(createdUser.getId()));

        User deletedUser = em.find(User.class, createdUser.getId());
        assertNull(deletedUser);
    }

    @Test
    void shouldDeleteUserIfUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.delete(999L));
    }

    @Test
    void shouldGetAllUsers() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("User Name");
        userDto1.setEmail("User@mail.com");

        UserDto userDto2 = new UserDto();
        userDto2.setName("Another User Name");
        userDto2.setEmail("AnotherUser@mail.com");

        userService.create(userDto1);
        userService.create(userDto2);

        List<UserDto> users = userService.getAll();

        assertEquals(2, users.size());
    }
}