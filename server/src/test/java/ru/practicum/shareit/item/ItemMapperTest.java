package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestByIdDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void shouldMapItemToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequest(new Request());

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void shouldMapItemToItemForRequestByIdDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);

        ItemForRequestByIdDto itemForRequestByIdDto = itemMapper.toItemForRequestByIdDto(item);

        assertNotNull(itemForRequestByIdDto);
        assertEquals(item.getId(), itemForRequestByIdDto.getId());
        assertEquals(item.getName(), itemForRequestByIdDto.getName());
        assertEquals(item.getDescription(), itemForRequestByIdDto.getDescription());
        assertEquals(item.getAvailable(), itemForRequestByIdDto.getAvailable());
    }

    @Test
    void shouldMapItemToItemByIdDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);

        BookingDto lastBooking = new BookingDto();
        BookingDto nextBooking = new BookingDto();
        List<CommentDto> comments = List.of(new CommentDto(1L, "Comment Text", "Author", null));

        ItemByIdDto itemByIdDto = itemMapper.toItemDto(item, comments, lastBooking, nextBooking);

        assertNotNull(itemByIdDto);
        assertEquals(item.getId(), itemByIdDto.getId());
        assertEquals(item.getName(), itemByIdDto.getName());
        assertEquals(item.getDescription(), itemByIdDto.getDescription());
        assertEquals(item.getAvailable(), itemByIdDto.getAvailable());
        assertEquals(comments, itemByIdDto.getComments());
        assertEquals(lastBooking, itemByIdDto.getLastBooking());
        assertEquals(nextBooking, itemByIdDto.getNextBooking());
    }

    @Test
    void shouldMapItemDtoToItem() {
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, 2L);
        User owner = new User();

        Item item = itemMapper.toItem(itemDto, owner);

        assertNotNull(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(owner, item.getOwner());
    }

    @Test
    void shouldMapItemDtoToItemWithRequest() {
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, 2L);
        User owner = new User();
        Request request = new Request();

        Item item = itemMapper.toItem(itemDto, owner, request);

        assertNotNull(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }
}

