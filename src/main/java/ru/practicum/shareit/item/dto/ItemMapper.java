package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingNestedDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

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

    public static ItemAllFieldsDto toItemDto(Item item, Collection<Comment> comments) {
        ItemAllFieldsDto itemAllFieldsDto = toItemDto(item);
        itemAllFieldsDto.setComments(comments
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList())
        );
        return itemAllFieldsDto;
    }

    public static ItemAllFieldsDto toItemDto(Item from,
                                             Collection<Comment> comments,
                                             BookingNestedDto lastBooking,
                                             BookingNestedDto nextBooking) {
        ItemAllFieldsDto itemAllFieldsDto = toItemDto(from, comments);

        if (lastBooking != null && lastBooking.getId() != null) {
            itemAllFieldsDto.setLastBooking(lastBooking);
        }
        if (nextBooking != null && nextBooking.getId() != null) {
            itemAllFieldsDto.setNextBooking(nextBooking);
        }
        return itemAllFieldsDto;
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
