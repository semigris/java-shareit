package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateBookingDto {
    private Long itemId;
    private Long userId;
    private LocalDateTime start;
    private LocalDateTime end;
}
