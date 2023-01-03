package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.NotValidException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {

    private int increment = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item get(long id) throws NotFoundException {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Вещь с идентификатором " +
                    id + " не зарегистрирована!");
        }
        return items.get(id);
    }

    @Override
    public Collection<Item> getAllByOwnerId(long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Item add(Item item) {
        if (item.getAvailable() == null) {
            throw new NotValidException("поле Доступность не может быть пустым!");
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new NotValidException("поле Название не может быть пустым!");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new NotValidException("поле Описание не может быть пустым!");
        }
        item.setId((long) ++increment);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item patch(Item item) throws NotFoundException {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Вещь с идентификатором " +
                    item.getId() + " не зарегистрирована!");
        }
        Item storedItem = items.get(item.getId());
        if (item.getName() != null && !item.getName().isEmpty()) {
            storedItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            storedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            storedItem.setAvailable(item.getAvailable());
        }
        items.put(storedItem.getId(), storedItem);
        return items.get(storedItem.getId());
    }

    @Override
    public boolean delete(long id) {
        items.remove(id);
        return !items.containsKey(id);
    }

    @Override
    public Collection<Item> search(String keyword, long userId) {
        Collection<Item> result = new ArrayList<>();
        if (!keyword.isEmpty()) {
            result.addAll(items.values().stream()
                    .filter(
                            item -> item.getAvailable() && (
                                    item.getName().toLowerCase().contains(keyword.toLowerCase())
                                            || item.getDescription().toLowerCase().contains(keyword.toLowerCase())
                            )
                    ).collect(Collectors.toList()));
        }
        return result;
    }
}
