package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    public static ItemAllFieldsDto toItemDto(Item item) {
        return new ItemAllFieldsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : 0
        );
    }

    public static Item toItem(ItemAllFieldsDto itemAllFieldsDto, User owner) {
         Item item = new Item();
         item.setName(itemAllFieldsDto.getName() == null ? "" : itemAllFieldsDto.getName());
         item.setDescription(itemAllFieldsDto.getDescription() == null ? "" : itemAllFieldsDto.getDescription());
         item.setAvailable(itemAllFieldsDto.getAvailable());
         item.setOwner(owner);
         return item;
    }
}
