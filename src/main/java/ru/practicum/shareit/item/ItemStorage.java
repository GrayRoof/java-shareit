package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item get(long id);
    Collection<Item> getAllByOwnerId(long ownerId);
    Item add(Item item);
    Item patch(Item item);
    boolean delete(long id);
    Collection<Item> search(String search, long userId);
}
