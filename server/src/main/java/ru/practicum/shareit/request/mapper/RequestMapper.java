package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemForRequestByIdDto;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Component
public class RequestMapper {
    public RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor())
                .created(request.getCreated())
                .build();
    }

    public Request toRequest(CreateRequestDto createRequestDto) {
        return Request.builder()
                .description(createRequestDto.getDescription())
                .build();
    }

    public RequestDto toRequestDto(Request request, List<ItemForRequestByIdDto> items) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor())
                .created(request.getCreated())
                .items(items)
                .build();
    }
}

