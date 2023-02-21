package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingToInputDto;

import javax.validation.Valid;
import java.util.Collection;

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
        log.info("SERVER BOOKING GET {}", bookingId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getCreated(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        log.info("SERVER BOOKING GET for user {} state {}. Pagination from {} limit {}", userId, state, from, size);
        return bookingService.getCreated(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getForOwnedItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        log.info("SERVER BOOKING GET for owner {} state {}. Pagination from {} limit {}", userId, state, from, size);
        return bookingService.getForOwnedItems(userId, state, from, size);
    }

    @PostMapping
    public BookingDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingToInputDto bookingToInputDto
            ) {
        log.info("SERVER BOOKING POST by user {} данные {}", userId, bookingToInputDto);
        return bookingService.create(userId, bookingToInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApproved(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam("approved") boolean approved
    ) {
        log.info("SERVER BOOKING PATCH {} by user {} approved {}", bookingId, userId, approved);
        return bookingService.setApproved(userId, bookingId, approved);
    }
}
