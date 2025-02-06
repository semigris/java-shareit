package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.user.dto.validationgroups.Create;
import ru.practicum.shareit.user.dto.validationgroups.Update;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя пользователя должно быть заполнено", groups = Create.class)
    private String name;

    @NotBlank(message = "email должно быть указан", groups = Create.class)
    @Email(message = "email должен быть корректным", groups = {Create.class, Update.class})
    private String email;
}
