package ru.practicum.shareit.item.service.impl;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        log.debug("Обработка запроса на добавление вещи: {}, пользователем: {}", itemDto, ownerId);
        Item item;
        User owner = userRepository.findById(ownerId).orElseThrow(() -> {
            log.warn("Попытка создать вещь с несуществующим владельцем id: {}", ownerId);
            return new NotFoundException("Пользователь с id: " + ownerId + " не найден");
        });

        if (itemDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            item = itemMapper.toItem(itemDto, owner, request);
        } else {
            item = itemMapper.toItem(itemDto, owner);
        }
        itemRepository.save(item);
        ItemDto createdItem = itemMapper.toItemDto(item);

        log.debug("Вещь успешно создана: {}", createdItem);
        return createdItem;
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        log.debug("Обработка запроса на обновление информации о вещи с id: {}, новые данные: {}, владельцем с id: {}", itemId, itemDto, ownerId);

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
            ItemDto updatedItem = itemMapper.toItemDto(item);
            log.debug("Вещь с id {} успешно обновлена: {}", itemId, updatedItem);
            return updatedItem;
        }

        log.warn("Попытка обновления вещи с id {} пользователем, не являющимся её владельцем: {}", itemId, ownerId);
        throw new NotFoundException("Вещь может быть обновлена только владельцем");
    }

    @Override
    @Transactional(readOnly = true)
    public ItemByIdDto getItemById(Long itemId, Long userId) {
        log.debug("Обработка запроса на получение информации о вещи по id: {} пользователем: {}", itemId, userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Попытка получения информации о несуществующей вещи с id: {}", itemId);
            return new NotFoundException("Вещь с id: " + itemId + " не найдена");
        });

        BookingDto lastBooking = bookingRepository.findLastBookingsByItemId(itemId).stream()
                .findFirst()
                .map(bookingMapper::toBookingDto)
                .orElse(null);

        BookingDto nextBooking = bookingRepository.findNextBookingsByItemId(itemId).stream()
                .findFirst()
                .map(bookingMapper::toBookingDto)
                .orElse(null);

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .toList();

        if (!item.getOwner().getId().equals(userId)) lastBooking = nextBooking = null;
        ItemByIdDto foundItem = itemMapper.toItemDto(item, comments, lastBooking, nextBooking);

        log.debug("Полная информации о вещи с id: {} пользователем: {} получена: {}", itemId, userId, foundItem);
        return foundItem;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItems(Long ownerId) {
        log.debug("Обработка запроса на получение информации о всех вещах пользователя с id: {}", ownerId);

        List<ItemDto> foundItems = itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId)).map(itemMapper::toItemDto).toList();

        log.debug("Для владельца с id: {} найдено вещей: {}. Список: {}", ownerId, foundItems.size(), foundItems);
        return foundItems;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        log.debug("Обработка запроса на поиск вещи: '{}'", text);

        if (StringUtils.isBlank(text)) {
            log.debug("Текст поиска пустой, возвращяем пустой список");
            return Collections.emptyList();
        }
        String lowerCaseText = text.toLowerCase();
        List<ItemDto> foundItems = itemRepository.findAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText) || item.getDescription().toLowerCase().contains(lowerCaseText))
                .filter(item -> item.getAvailable().equals(true)).map(itemMapper::toItemDto).toList();

        log.debug("По запросу: '{}' найдено вещей: {}. Список: {}", text, foundItems.size(), foundItems);
        return foundItems;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        log.debug("Обработка запроса на добавление комментария на вещь: {}", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id: " + itemId + " не найдена"));
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + userId + " не найден"));

        List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(itemId, userId);

        boolean hasRented = bookings.stream()
                .anyMatch(booking -> booking.getStatus() == Booking.Status.APPROVED);
        if (!hasRented) {
            throw new NotValidException("Пользователь не может оставить комментарий, так как не брал эту вещь в аренду.");
        }

        boolean hasFinishedRent = bookings.stream()
                .anyMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()));
        if (!hasFinishedRent) {
            throw new NotValidException("Пользователь не может оставить комментарий, так как аренда вещи еще не завершена.");
        }

        Comment comment = commentMapper.toComment(commentDto, author, item);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(author);

        commentRepository.save(comment);
        CommentDto createdComment = commentMapper.toCommentDto(comment);

        log.debug("Комментарий успешно создан: {}", createdComment);
        return createdComment;
    }
}
