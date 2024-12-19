package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    public Item save(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public void delete(Long id) {
        items.remove(id);
    }
}
