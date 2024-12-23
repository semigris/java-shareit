package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    public User save(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    public User update(Long id, User user) {
        return users.put(id, user);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean deleteById(Long id) {
        return users.remove(id) != null;
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }
}
