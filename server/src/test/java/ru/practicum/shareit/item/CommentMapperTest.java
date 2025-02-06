package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void shouldMapCommentToCommentDto() {
        User author = new User();
        author.setName("Author Name");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("This is a comment");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    void shouldMapCommentDtoToComment() {
        User author = new User();
        author.setName("Author Name");

        Item item = new Item();
        item.setName("Item Name");

        CommentDto commentDto = new CommentDto();
        commentDto.setText("This is a comment");
        commentDto.setCreated(LocalDateTime.now());

        Comment comment = commentMapper.toComment(commentDto, author, item);

        assertNotNull(comment);
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(author, comment.getAuthor());
        assertEquals(item, comment.getItem());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }
}

