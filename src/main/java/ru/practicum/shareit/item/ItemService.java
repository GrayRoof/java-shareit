package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto get(long id);

    Collection<ItemDto> getAllByUserId(long userId);

    ItemDto add(ItemDto itemDto, long ownerId);

    ItemDto patch(ItemDto itemDto, long itemId, long userId);

    boolean delete(long id);

    Collection<ItemDto> search(String text, long userId);
}
