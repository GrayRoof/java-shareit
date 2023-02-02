package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingToInputDto bookingToInputDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingToInputDto.getStart());
        booking.setEnd(bookingToInputDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public static BookingNestedDto toBookingNestedDto(Booking booking) {
        BookingNestedDto newBookingDto = new BookingNestedDto();
        if (booking != null) {
            newBookingDto.setId(booking.getId());
            newBookingDto.setBookerId(booking.getBooker().getId());
        }
        return newBookingDto;
    }
}
