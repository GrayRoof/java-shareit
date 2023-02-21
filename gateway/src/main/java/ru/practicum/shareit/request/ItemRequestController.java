package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestToInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id")long userId,
                                             @PathVariable long requestId) {
        log.info("GATEWAY REQUEST GET {} user {}", requestId, userId);
        return itemRequestClient.get(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id")long userId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "20") @Positive int size) {
        log.info("GATEWAY REQUEST GET all from {} limit {} user {}", from, size, userId);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id")long userId) {
        log.info("GATEWAY REQUEST GET by User ID {}", userId);
        return itemRequestClient.getByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader("X-Sharer-User-Id")long userId,
                                        @Valid @RequestBody ItemRequestToInputDto itemRequestDto) {
        log.info("GATEWAY REQUEST POST body {} user {}", itemRequestDto.getDescription(), userId);
        return itemRequestClient.add(userId, itemRequestDto);
    }
}

