package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingToInputDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto get(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId
    ) {
        log.info("BOOKING GET {}", bookingId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getCreated(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("BOOKING GET for user {} state {}", userId, state);
        return bookingService.getCreated(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getForOwnedItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("BOOKING GET for owner {} state {}", userId, state);
        return bookingService.getForOwnedItems(userId, state);
    }

    @PostMapping
    public BookingDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingToInputDto bookingToInputDto
            ) {
        log.info("BOOKING POST by user {} данные {}", userId, bookingToInputDto);
        return bookingService.create(userId, bookingToInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApproved(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam("approved") boolean approved
    ) {
        log.info("BOOKING PATCH {} by user {} approved {}", bookingId, userId, approved);
        return bookingService.setApproved(userId, bookingId, approved);
    }
}
