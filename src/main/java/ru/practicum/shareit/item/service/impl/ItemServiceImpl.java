package ru.practicum.shareit.item.service.impl;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.debug("Создание вещи: {}, владельцем с id: {}", itemDto, ownerId);
        User owner = userRepository.findById(ownerId).orElseThrow(() -> {
            log.warn("Попытка создать вещь с несуществующим владельцем id: {}", ownerId);
            return new NotFoundException("Пользователь с id: " + ownerId + " не найден");
        });
        Item item = ItemMapper.toItem(itemDto, owner, null);
        itemRepository.save(item);
        ItemDto createdItem = ItemMapper.toItemDto(item);
        log.debug("Вещь успешно создана: {}", createdItem);
        return createdItem;
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        log.debug("Обновление вещи с id: {}, новые данные: {}, владельцем с id: {}", itemId, itemDto, ownerId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Попытка обновления несуществующей вещи с id: {}", itemId);
            return new NotFoundException("Вещь с id: " + itemId + " не найдена");
        });
        if (item.getOwner().getId().equals(ownerId)) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            itemRepository.save(item);
            ItemDto updatedItem = ItemMapper.toItemDto(item);
            log.debug("Вещь с id {} успешно обновлена: {}", itemId, updatedItem);
            return updatedItem;
        }
        log.warn("Попытка обновления вещи с id {} пользователем, не являющимся её владельцем: {}", itemId, ownerId);
        throw new NotFoundException("Вещь может быть обновлена только владельцем");
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.debug("Получение информации о вещи с id: {}", itemId);
        return itemRepository.findById(itemId).map(item -> {
            ItemDto foundItem = ItemMapper.toItemDto(item);
            log.debug("Вещь с id: {} найдена: {}", itemId, foundItem);
            return foundItem;
        }).orElseThrow(() -> {
            log.warn("Вещь с id: {} не найдена", itemId);
            return new NotFoundException("Вещь с id: " + itemId + " не найдена");
        });
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        log.debug("Получение всех вещей владельца с id: {}", ownerId);
        List<ItemDto> foundItems = itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId)).map(ItemMapper::toItemDto).toList();
        log.debug("Для владельца с id: {} найдено вещей: {}. Список: {}", ownerId, foundItems.size(), foundItems);
        return foundItems;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.debug("Поиск вещей по тексту: '{}'", text);
        if (StringUtils.isBlank(text)) {
            log.debug("Текст поиска пустой, возвращяем пустой список");
            return Collections.emptyList();
        }
        String lowerCaseText = text.toLowerCase();
        List<ItemDto> foundItems = itemRepository.findAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText) || item.getDescription().toLowerCase().contains(lowerCaseText))
                .filter(item -> item.getAvailable().equals(true)).map(ItemMapper::toItemDto).toList();
        log.debug("По запросу: '{}' найдено вещей: {}. Список: {}", text, foundItems.size(), foundItems);
        return foundItems;
    }
}
