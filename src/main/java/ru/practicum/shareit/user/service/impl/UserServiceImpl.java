package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto save(UserDto userDto) {
        log.debug("Создание пользователя: {}", userDto);

        User user = userMapper.toUser(userDto);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Попытка создания пользователя с уже существующей электронной почтой: {}", user.getEmail());
            throw new NotValidException("Пользователь с такой электронной почтой уже существует");
        }
        userRepository.save(user);
        UserDto savedUser = userMapper.toUserDto(user);

        log.debug("Пользователь успешно создан: {}", savedUser);
        return savedUser;
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("Получение информации о всех пользователях");

        List<UserDto> foundUsers = userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .toList();

        log.debug("Список найденных пользователей: {}", foundUsers);
        return foundUsers;
    }

    @Override
    public UserDto getById(Long id) {
        log.debug("Получение информации о пользователе по id: " + id);

        UserDto foundUser = userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        log.debug("По id {} найден пользователь: {}", id, foundUser);
        return foundUser;
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.debug("Обновление данных пользователя по id: {}, новые данные: {}", id, userDto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка обновления несуществующего пользователя с id: {}", id);
                    return new IllegalArgumentException("Пользователь не найден");
                });

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                log.warn("Попытка создания пользователя с уже существующей электронной почтой: {}", user.getEmail());
                throw new NotValidException("Пользователь с такой электронной почтой уже существует");
            }
            user.setEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
        UserDto updatedUser = userMapper.toUserDto(user);

        log.debug("Пользователь с id {} успешно обновлён: {}", id, updatedUser);
        return updatedUser;
    }

    @Override
    public void delete(Long id) {
        log.debug("Удаление пользователя с id: {}", id);

        if (userRepository.findById(id).isEmpty()) {
            log.warn("Попытка удаления несуществующего пользователя с id: {}", id);
            throw new IllegalArgumentException("Пользователь не найден");
        }
        userRepository.deleteById(id);

        log.debug("Пользователь с id {} успешно удалён", id);
    }
}
