package ru.practicum.shereit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.CreateRequestDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {
        ItemDto invalidItemDto = new ItemDto();
        invalidItemDto.setDescription("Item Description");
        invalidItemDto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(invalidItemDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Название должно быть заполнено")));
    }

    @Test
    void shouldFailValidationWhenDescriptionIsBlank() {
        ItemDto invalidItemDto = new ItemDto();
        invalidItemDto.setName("Item Name");
        invalidItemDto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(invalidItemDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Описание должно быть заполнено")));
    }

    @Test
    void shouldFailValidationWhenAvailableIsNull() {
        ItemDto invalidItemDto = new ItemDto();
        invalidItemDto.setName("Item Name");
        invalidItemDto.setDescription("Item Description");

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(invalidItemDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Доступность должна быть указана")));
    }
}

