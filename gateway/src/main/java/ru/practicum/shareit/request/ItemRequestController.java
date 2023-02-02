package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.request.dto.ItemRequestToInputDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestService;

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id")long userId,
                                             @PathVariable long requestId) throws NotFoundException {
        log.info("REQUEST GET {} user {}", requestId, userId);
        return itemRequestService.get(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id")long userId,
                                                     @RequestParam(required = false, defaultValue = "0") int from,
                                                     @RequestParam(required = false, defaultValue = "20") int size)
            throws NotValidException, NotFoundException {
        log.info("REQUEST GET all from {} limit {} user {}", userId, from, size);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id")long userId)
            throws NotFoundException {
        log.info("REQUEST GET by User ID {}", userId);
        return itemRequestService.getByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader("X-Sharer-User-Id")long userId,
                                        @Valid @RequestBody ItemRequestToInputDto itemRequestDto) throws NotFoundException {
        log.info("REQUEST POST body {} user {}", itemRequestDto.getDescription(), userId);
        return itemRequestService.add(userId, itemRequestDto);
    }
}

