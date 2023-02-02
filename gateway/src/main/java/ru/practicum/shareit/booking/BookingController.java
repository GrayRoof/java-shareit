package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingToInputDto;

import javax.validation.Valid;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId
    ) {
        log.info("BOOKING GET {}", bookingId);
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getCreated(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        log.info("BOOKING GET for user {} state {}. Pagination from {} limit {}", userId, state, from, size);
        return bookingClient.getCreated(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getForOwnedItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        log.info("BOOKING GET for owner {} state {}. Pagination from {} limit {}", userId, state, from, size);
        return bookingClient.getForOwnedItems(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingToInputDto bookingToInputDto
    ) {
        log.info("BOOKING POST by user {} данные {}", userId, bookingToInputDto);
        return bookingClient.create(userId, bookingToInputDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApproved(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam("approved") boolean approved
    ) {
        log.info("BOOKING PATCH {} by user {} approved {}", bookingId, userId, approved);
        return bookingClient.setApproved(userId, bookingId, approved);
    }
}

