package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = "jdbc.url=jdbc:postgresql://localhost:5432/test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    @PersistenceContext
    private EntityManager em;

    @Test
    void shouldCreateItem() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        assertNotNull(createdItem);
        assertEquals("Item", createdItem.getName());
        assertEquals("Item Description", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
    }

    @Test
    void shouldFailCreateItemIfOwnerNotFound() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        Exception exception = assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 999L));
        assertEquals("Пользователь с id: 999 не найден", exception.getMessage());
    }

    @Test
    void shouldFailCreateItemIfRequestNotFound() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(9L);

        Exception exception = assertThrows(NotFoundException.class, () -> itemService.create(itemDto, createdOwner.getId()));
        assertEquals("Запрос не найден", exception.getMessage());
    }

    @Test
    void shouldUpdateItem() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        createdItem.setName("Updated Item");
        createdItem.setDescription("Updated Description");

        ItemDto updatedItem = itemService.update(createdItem.getId(), createdItem, createdOwner.getId());

        assertNotNull(updatedItem);
        assertEquals("Updated Item", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
    }

    @Test
    void shouldFailtUpdateIfNotOwner() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto anotherUser = new UserDto();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@mail.com");
        UserDto createdAnotherUser = userService.create(anotherUser);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        createdItem.setName("Updated Item");
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.update(createdItem.getId(), createdItem, createdAnotherUser.getId()));

        assertEquals("Вещь может быть обновлена только владельцем", exception.getMessage());
    }

    @Test
    void shouldFailtUpdateIfItemNotFound() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        Exception exception = assertThrows(NotFoundException.class, () -> itemService.update(999L, createdItem, createdOwner.getId()));
        assertEquals("Вещь с id: 999 не найдена", exception.getMessage());
    }

    @Test
    void shouldGetItem() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        ItemByIdDto foundItem = itemService.getItemById(createdItem.getId(), createdOwner.getId());

        assertNotNull(foundItem);
        assertEquals("Item", foundItem.getName());
    }

    @Test
    void shouldGetItemIfItemNotFound() {
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.getItemById(999L, 1L));
        assertEquals("Вещь с id: 999 не найдена", exception.getMessage());
    }


    @Test
    void shouldGetAllItems() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Item");
        itemDto1.setDescription("Item Description");
        itemDto1.setAvailable(true);
        itemService.create(itemDto1, createdOwner.getId());

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Another Item");
        itemDto2.setDescription("Another Item Description");
        itemDto2.setAvailable(true);
        itemService.create(itemDto2, createdOwner.getId());

        List<ItemDto> items = itemService.getAllItems(createdOwner.getId());

        assertEquals(2, items.size());
    }

    @Test
    void shouldSearchItem() {
        UserDto owner = new UserDto();
        owner.setName("User Name");
        owner.setEmail("User@mail.com");
        UserDto createdOwner = userService.create(owner);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Item");
        itemDto1.setDescription("Item Description");
        itemDto1.setAvailable(true);
        itemService.create(itemDto1, createdOwner.getId());

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Another");
        itemDto2.setDescription("Another Description");
        itemDto2.setAvailable(true);
        itemService.create(itemDto2, createdOwner.getId());

        List<ItemDto> results = itemService.searchItems("Item");
        assertEquals(1, results.size());
        assertEquals("Item", results.get(0).getName());
    }

    @Test
    void shouldNotSearchItemsIfTextIsBlank() {
        List<ItemDto> results = itemService.searchItems("");
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldAddComment() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("Owner@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto user = new UserDto();
        user.setName("User");
        user.setEmail("User@mail.com");
        UserDto createdUser = userService.create(user);

        ItemDto item = new ItemDto();
        item.setName("Item");
        item.setDescription("Item Description");
        item.setAvailable(true);
        ItemDto createdItem = itemService.create(item, createdOwner.getId());


        CommentDto comment = new CommentDto();
        comment.setText("Item Comment");
        comment.setAuthorName(user.getName());
        comment.setCreated(LocalDateTime.now());
        assertThrows(NotValidException.class, () -> itemService.addComment(createdItem.getId(), createdUser.getId(), comment));
    }

    @Test
    void shouldFailAddCommentIfItemNotFound() {
        CommentDto comment = new CommentDto();
        comment.setText("Item Comment");
        comment.setAuthorName("user Name");
        comment.setCreated(LocalDateTime.now());

        Exception exception = assertThrows(NotFoundException.class, () -> itemService.addComment(999L, 1L, comment));
        assertEquals("Вещь с id: 999 не найдена", exception.getMessage());
    }

    @Test
    void shouldFailAddCommentIfUserNotFound() {
        UserDto user = new UserDto();
        user.setName("User");
        user.setEmail("User@mail.com");
        UserDto createdUser = userService.create(user);

        ItemDto item = new ItemDto();
        item.setName("Item");
        item.setDescription("Item Description");
        item.setAvailable(true);
        ItemDto createdItem = itemService.create(item, createdUser.getId());

        CommentDto comment = new CommentDto();
        comment.setText("Item Comment");
        comment.setAuthorName(createdUser.getName());
        comment.setCreated(LocalDateTime.now());

        Exception exception = assertThrows(NotFoundException.class, () -> itemService.addComment(createdItem.getId(), 66L, comment));
        assertEquals("Пользователь с id: 66 не найден", exception.getMessage());
    }

    @Test
    void shouldFailAddCommentIfUserNotBookedItem() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto user = new UserDto();
        user.setName("User");
        user.setEmail("user@mail.com");
        UserDto createdUser = userService.create(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CommentDto comment = new CommentDto();
        comment.setText("Item Comment");

        Exception exception = assertThrows(NotValidException.class, () -> itemService.addComment(createdItem.getId(), createdUser.getId(), comment));

        assertEquals("Пользователь не может оставить комментарий, так как не брал эту вещь в аренду.", exception.getMessage());
    }

    @Test
    void shouldFailAddCommentIfBookingNotFinished() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        UserDto createdOwner = userService.create(owner);

        UserDto user = new UserDto();
        user.setName("User");
        user.setEmail("user@mail.com");
        UserDto createdUser = userService.create(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.create(itemDto, createdOwner.getId());

        CreateBookingDto bookingDto = new CreateBookingDto(createdItem.getId(), createdUser.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        BookingDto createdBooking = bookingService.create(bookingDto);

        bookingService.update(createdBooking.getId(), true, createdOwner.getId());

        CommentDto comment = new CommentDto();
        comment.setText("Item Comment");

        Exception exception = assertThrows(NotValidException.class, () -> itemService.addComment(createdItem.getId(), createdUser.getId(), comment));

        assertEquals("Пользователь не может оставить комментарий, так как аренда вещи еще не завершена.", exception.getMessage());
    }
}