package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateRequestDto {
    private Long userId;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}

