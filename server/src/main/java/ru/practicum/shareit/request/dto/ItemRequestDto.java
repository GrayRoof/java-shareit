package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
public class ItemRequestDto {
    long id;
    String description;
    User requester;
    LocalDateTime created;
    Collection<ItemAllFieldsDto> items;
}
