package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequestDto {
    private Long userId;

    @NotBlank(message = "Описание должно быть заполнено")
    private String description;

    private LocalDateTime created = LocalDateTime.now();
}
