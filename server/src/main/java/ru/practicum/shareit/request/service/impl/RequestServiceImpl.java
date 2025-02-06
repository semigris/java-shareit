package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestByIdDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper requestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public RequestDto create(CreateRequestDto createRequestDto) {
        log.debug("Обработка запроса на создание запроса на вещь пользователем: {}", createRequestDto);
        User user = userRepository.findById(createRequestDto.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Request request = requestMapper.toRequest(createRequestDto);
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        requestRepository.save(request);
        RequestDto createdRequest = requestMapper.toRequestDto(request);

        log.debug("Запрос на бронирование успешно создан: {}", createdRequest);
        return createdRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDto getRequestById(Long requestId, Long userId) {
        log.debug("Обработка запроса на получение данных о запросе: {} пользователем: {}", requestId, userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<ItemForRequestByIdDto> itemList = itemRepository.findByRequestId(requestId).stream()
                .map(itemMapper::toItemForRequestByIdDto)
                .toList();

        RequestDto foundRequest = requestMapper.toRequestDto(request, itemList);
        log.debug("Получены данные о запросе: {}", foundRequest);
        return foundRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getOwnRequests(Long userId) {
        log.debug("Обработка запроса на получение списка своих запросов пользователем: {}", userId);
        return requestRepository.findByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequests(Long userId, int from, int size) {
        log.debug("Обработка запроса на получение списка запросов других пользователей пользователем: {}", userId);
        return requestRepository.findAllByRequestorIdNot(userId, PageRequest.of(from / size, size, Sort.by("created").descending()))
                .stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }
}
