package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(Long itemId, ItemDto itemDto, Long ownerId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItems(Long ownerId);

    List<ItemDto> searchItems(String text);
}
