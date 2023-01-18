package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemToInputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, Collection<Item> items) {
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        itemRequestDto.setItems(items
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList())
        );
        return itemRequestDto;
    }
}
