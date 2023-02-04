package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingNestedDto;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;

@Data
@RequiredArgsConstructor
public class ItemAllFieldsDto {
    Long id;
    @NotEmpty
    String name;
    @NotEmpty
    String description;
    Boolean available;
    Collection<CommentDto> comments;
    BookingNestedDto lastBooking;
    BookingNestedDto nextBooking;
    Long requestId;

    public ItemAllFieldsDto(long id, String name, String description, Boolean available, long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = request;
    }
}
