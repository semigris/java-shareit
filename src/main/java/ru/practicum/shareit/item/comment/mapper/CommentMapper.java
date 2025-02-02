package ru.practicum.shareit.item.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "author.name", target = "authorName")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentDto commentDto);
}
