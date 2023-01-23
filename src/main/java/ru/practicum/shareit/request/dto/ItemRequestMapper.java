package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setRequester(itemRequest.getRequester());
        return itemRequestDto;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, Collection<ItemAllFieldsDto> items) {
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }
}
