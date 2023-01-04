package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemToInputDto {
    String name;
    String description;
    Boolean available;
}