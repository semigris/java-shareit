package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ItemForRequestByIdDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
