package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto get(
            @RequestHeader() long userId,
            @PathVariable long bookingId
    ) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getCreated(
            @RequestHeader() long callerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getCreated(callerId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getForOwnedItems(
            @RequestHeader() long callerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getForOwnedItems(callerId, state);
    }
}
