package ru.practicum.shereit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.validationgroups.Create;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {
        UserDto invalidUserDto = new UserDto();
        invalidUserDto.setEmail("Email@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(invalidUserDto, Create.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Имя пользователя должно быть заполнено")));
    }

    @Test
    void shouldFailValidationWhenEmailIsInvalid() {
        UserDto invalidUserDto = new UserDto();
        invalidUserDto.setName("Item Name");
        invalidUserDto.setEmail("email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(invalidUserDto, Create.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("email должен быть корректным")));
    }
}

