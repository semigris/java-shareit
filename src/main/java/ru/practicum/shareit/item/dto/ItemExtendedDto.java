package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemExtendedDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private BookingDto lastBooking;
    private BookingDto nextBooking;

    @Override
    public String toString() {
        return "id=" + id +
               ", name='" + name +
               "', description='" + description +
               "', available=" + available +
               "', comments=" + comments +
               "', lastBooking=" + lastBooking +
               "', nextBooking=" + nextBooking + "'";
    }
}
