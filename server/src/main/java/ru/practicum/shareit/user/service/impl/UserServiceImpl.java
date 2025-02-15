package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableDataException;
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
    @Transactional
    public UserDto create(UserDto userDto) {
        log.debug("Обработка запроса на создание пользователя: {}", userDto);

        User user = userMapper.toUser(userDto);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Попытка создания пользователя с уже существующей электронной почтой: {}", user.getEmail());
            throw new UnavailableDataException("Пользователь с такой электронной почтой уже существует");
        }
        userRepository.save(user);
        UserDto createdUser = userMapper.toUserDto(user);

        log.debug("Пользователь успешно создан: {}", createdUser);
        return createdUser;
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        log.debug("Обработка запроса на обновление данных пользователя по id: {}, новые данные: {}", id, userDto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка обновления несуществующего пользователя с id: {}", id);
                    return new NotFoundException("Пользователь не найден");
                });

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                log.warn("Попытка создания пользователя с уже существующей электронной почтой: {}", user.getEmail());
                throw new UnavailableDataException("Пользователь с такой электронной почтой уже существует");
            }
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
        UserDto updatedUser = userMapper.toUserDto(user);

        log.debug("Пользователь с id {} успешно обновлён: {}", id, updatedUser);
        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        log.debug("Обработка запроса на получение информации о пользователе по id: " + id);

        UserDto foundUser = userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        log.debug("По id {} найден пользователь: {}", id, foundUser);
        return foundUser;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Обработка запроса на удаление пользователя по id: {}", id);

        if (userRepository.findById(id).isEmpty()) {
            log.warn("Попытка удаления несуществующего пользователя с id: {}", id);
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);

        log.debug("Пользователь с id {} успешно удалён", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        log.debug("Обработка запроса на получение информации о всех пользователях");

        List<UserDto> foundUsers = userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .toList();

        log.debug("Список найденных пользователей: {}", foundUsers);
        return foundUsers;
    }
}
