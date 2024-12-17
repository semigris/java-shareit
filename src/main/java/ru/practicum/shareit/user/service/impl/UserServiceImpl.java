package ru.practicum.shareit.user.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new NotValidException("Пользователь с такой электронной почтой уже существует.");
        }
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return ((List<User>) userRepository.findAll())
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new NotValidException("Пользователь с такой электронной почтой уже существует.");
            }
            user.setEmail(userDto.getEmail());
        }

        User userForUpdate = UserMapper.toUser(userDto);

        userRepository.update(id, userForUpdate);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
