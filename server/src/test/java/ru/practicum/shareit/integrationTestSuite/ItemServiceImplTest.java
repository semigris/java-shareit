package ru.practicum.shareit.integrationTestSuite;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

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
    void shouldCreateItemIfOwnerNotFound() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 999L));
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
        assertThrows(NotFoundException.class, () -> itemService.getItemById(999L, 1L));
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
        comment.setText("Comment about Item");
        comment.setAuthorName(user.getName());
        comment.setCreated(LocalDateTime.now());
        assertThrows(NotValidException.class, () -> itemService.addComment(createdItem.getId(), createdUser.getId(), comment));
    }
}