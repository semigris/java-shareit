package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemForRequestByIdDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RequestDto {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    List<ItemForRequestByIdDto> items;
}
