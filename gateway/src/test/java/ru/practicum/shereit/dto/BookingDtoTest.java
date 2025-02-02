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
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class BookingDtoTest {

    @Autowired
    private JacksonTester<CreateBookingDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidationWhenItemIdIsNull() {
        CreateBookingDto invalidBookingDto = new CreateBookingDto();
        invalidBookingDto.setStart(LocalDateTime.now().plusDays(1));
        invalidBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(invalidBookingDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Идентификатор вещи должен быть указан")));
    }

    @Test
    void shouldFailValidationWhenStartIsNull() {
        CreateBookingDto invalidBookingDto = new CreateBookingDto();
        invalidBookingDto.setItemId(1L);
        invalidBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(invalidBookingDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Дата начала бронирования должна быть указана")));
    }

    @Test
    void shouldFailValidationWhenEndIsNull() {
        CreateBookingDto invalidBookingDto = new CreateBookingDto();
        invalidBookingDto.setItemId(1L);
        invalidBookingDto.setStart(LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(invalidBookingDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Дата окончания бронирования должна быть указана")));
    }
    @Test
    void shouldFailValidationWhenEndIsBeforeNow() {
        CreateBookingDto invalidBookingDto = new CreateBookingDto();
        invalidBookingDto.setItemId(1L);
        invalidBookingDto.setStart(LocalDateTime.now().plusDays(2));
        invalidBookingDto.setEnd(LocalDateTime.now().minusDays(2));

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(invalidBookingDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Дата окончания бронирования должна быть в будущем")));
    }

    @Test
    void shouldFailValidationWhenStartIsBeforeNow() {
        CreateBookingDto invalidBookingDto = new CreateBookingDto();
        invalidBookingDto.setItemId(1L);
        invalidBookingDto.setStart(LocalDateTime.now().minusDays(2));
        invalidBookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(invalidBookingDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Дата начала бронирования не может быть в прошлом")));
    }
}