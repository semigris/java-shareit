package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemForRequestByIdDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RequestDto {
    private Long id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
    List<ItemForRequestByIdDto> items;
}
