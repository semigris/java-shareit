package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название должно быть заполнено")
    private String name;

    @NotBlank(message = "Описание должно быть заполнено")
    private String description;

    @NotNull(message = "Доступность должна быть указана")
    private Boolean available;

    private Long requestId;
}
