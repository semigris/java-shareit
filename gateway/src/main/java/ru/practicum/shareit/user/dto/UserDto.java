package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.user.dto.validationGroups.Create;
import ru.practicum.shareit.user.dto.validationGroups.Update;

@Getter
@Setter
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
