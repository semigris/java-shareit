package ru.practicum.shareit.item.service.impl;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
public class ItemServiceImpl implements ru.practicum.shareit.item.service.ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        if (item.getOwner().getId().equals(ownerId)) {
            if (itemDto.getName() != null) item.setName(itemDto.getName());
            if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
            if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
            itemRepository.save(item);
            return ItemMapper.toItemDto(item);
        }
        throw new NotFoundException("Вещь может быть обновлена только владельцем");
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(
                itemRepository.findById(itemId)
                        .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена")));
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        String lowerCaseText = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText)
                                || item.getDescription().toLowerCase().contains(lowerCaseText))
                .filter(item -> item.getAvailable().equals(true))
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
