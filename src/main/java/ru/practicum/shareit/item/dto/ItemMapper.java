package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    public static ItemToReturnDto toItemDto(Item item) {
        return new ItemToReturnDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : 0
        );
    }

    public static Item toItem(ItemToInputDto itemToInputDto, User owner) {
         Item item = new Item();
         item.setName(itemToInputDto.getName() == null ? "" : itemToInputDto.getName());
         item.setDescription(itemToInputDto.getDescription() == null ? "" : itemToInputDto.getDescription());
         item.setAvailable(itemToInputDto.getAvailable());
         item.setOwner(owner);
         return item;
    }
}
