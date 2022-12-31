package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exception.ForbiddenException;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.NotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;


    @Override
    public ItemDto get(long id) throws NotFoundException {
        Item item = itemRepository.require(id);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllByUserId(long userId) {
       return itemRepository.findAllByOwnerOrderByIdAsc(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) throws NotFoundException {
        userService.get(userId);
        if (Objects.isNull(itemDto.getAvailable())) {
            throw new NotValidException("Поле Доступность не должно быть пустым!");
        }
        Item item = ItemMapper.toItem(itemDto, userId);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto patch(ItemDto itemDto, long itemId, long userId) throws ForbiddenException, NotFoundException {
        Item storedItem = itemRepository.findByIdOrderByIdDesc(itemId);
        if (storedItem.getOwner() != userId) {
            throw new ForbiddenException("Владелец вещи не совпадает с пользователем " + userId + ". " +
                    "Изменить вещь может только владелец!");
        } else {

            itemDto.setId(itemId);
            Item patchedItem = itemRepository.save(ItemMapper.toItem(itemDto, userId));
            ItemDto patchedItemDto = ItemMapper.toItemDto(patchedItem);
            return patchedItemDto;
        }
    }

    @Override
    public void delete(long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public Collection<ItemDto> search(String text, long userId) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<ItemDto> result = itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return result;
    }
}
