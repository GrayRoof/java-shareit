package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exception.ForbiddenException;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto get(long id) {
        ItemDto itemDto = ItemMapper.toItemDto(itemStorage.get(id));
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllByUserId(long userId) {
       return itemStorage.getAllByOwnerId(userId)
               .stream()
               .map(ItemMapper::toItemDto)
               .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        userStorage.get(userId);
        Item newItem = itemStorage.add(ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto patch(ItemDto itemDto, long itemId, long userId) throws NotFoundException {
        Item storedItem = itemStorage.get(itemId);
        if (storedItem.getOwner() != userId) {
            throw new ForbiddenException("Владелец вещи не совпадает с пользователем " + userId + ". " +
                    "Изменить вещь может только владелец!");
        } else {

            itemDto.setId(itemId);
            Item patchedItem = itemStorage.patch(ItemMapper.toItem(itemDto, userId));
            ItemDto patchedItemDto = ItemMapper.toItemDto(patchedItem);
            return patchedItemDto;
        }
    }

    @Override
    public boolean delete(long id) {
        itemStorage.get(id);
        return itemStorage.delete(id);
    }

    @Override
    public Collection<ItemDto> search(String text, long userId) {
        Collection<ItemDto> result = itemStorage.search(text, userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return result;
    }
}
