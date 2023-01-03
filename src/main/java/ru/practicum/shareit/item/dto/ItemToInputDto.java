package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemToInputDto {
    String name;
    String description;
    @NotNull
    Boolean available;
}