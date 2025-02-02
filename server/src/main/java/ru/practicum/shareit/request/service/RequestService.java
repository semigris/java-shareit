package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(CreateRequestDto createRequestDto);

    List<RequestDto> getOwnRequests(Long userId);

    List<RequestDto> getAllRequests(Long userId, int from, int size);

    RequestDto getRequestById(Long requestId, Long userId);
}
