package ru.practicum.shereit.item;

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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@ContextConfiguration(classes = ShareItGateway.class)
class ItemControllerTest {

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldCreateItem() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");

        ItemDto createdItemDto = new ItemDto();
        createdItemDto.setId(1L);
        createdItemDto.setName("Item");

        when(itemClient.createItem(any(ItemDto.class), eq(userId))).thenReturn(ResponseEntity.ok(createdItemDto));

        ResponseEntity<Object> response = itemController.createItem(itemDto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Item", ((ItemDto) response.getBody()).getName());
    }

    @Test
    void shouldUpdateItem() {
        Long itemId = 1L;
        Long userId = 2L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(itemId);
        updatedItemDto.setName("Updated Item");

        when(itemClient.updateItem(eq(itemId), any(ItemDto.class), eq(userId))).thenReturn(ResponseEntity.ok(updatedItemDto));

        ResponseEntity<Object> response = itemController.updateItem(itemId, itemDto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Item", ((ItemDto) response.getBody()).getName());
    }

    @Test
    void shouldGetItem() {
        Long itemId = 1L;
        Long userId = 2L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Item");

        when(itemClient.getItem(eq(itemId), eq(userId))).thenReturn(ResponseEntity.ok(itemDto));

        ResponseEntity<Object> response = itemController.getItem(itemId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Item", ((ItemDto) response.getBody()).getName());
    }

    @Test
    void shouldGetAllItems() {
        Long userId = 2L;
        List<ItemDto> items = List.of(new ItemDto(), new ItemDto());

        when(itemClient.getAllItems(eq(userId))).thenReturn(ResponseEntity.ok(items));

        ResponseEntity<Object> response = itemController.getAllItems(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldSearchItem() {
        String searchText = "Test";
        List<ItemDto> items = List.of(new ItemDto());

        when(itemClient.searchItem(eq(searchText))).thenReturn(ResponseEntity.ok(items));

        ResponseEntity<Object> response = itemController.searchItem(searchText);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldAddComment() {
        Long itemId = 1L;
        Long userId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Item Comment");

        when(itemClient.addComment(eq(itemId), any(CommentDto.class), eq(userId))).thenReturn(ResponseEntity.ok("Comment added"));

        ResponseEntity<Object> response = itemController.addComment(itemId, commentDto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment added", response.getBody());
    }
}

