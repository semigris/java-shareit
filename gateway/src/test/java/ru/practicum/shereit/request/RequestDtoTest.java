package ru.practicum.shereit.request;

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
import ru.practicum.shareit.request.dto.CreateRequestDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class RequestDtoTest {

    @Autowired
    private JacksonTester<CreateRequestDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidationWhenDescriptionIsBlank() {
        CreateRequestDto invalidRequestDto = new CreateRequestDto();
        invalidRequestDto.setUserId(1L);
        invalidRequestDto.setDescription("");

        Set<ConstraintViolation<CreateRequestDto>> violations = validator.validate(invalidRequestDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Описание должно быть заполнено")));
    }
}
