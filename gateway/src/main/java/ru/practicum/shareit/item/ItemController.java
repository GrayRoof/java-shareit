package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentToInputDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemToInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getId(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long itemId)  {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(required = false, defaultValue = "0") int from,
                                                       @RequestParam(required = false, defaultValue = "20") int size) {
        return itemClient.getAll(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody ItemAllFieldsDto itemDto) {
        return itemClient.addItem(itemDto, userId);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                             @Valid @RequestBody CommentToInputDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.addComment(itemId, commentDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemToInputDto itemDto) {
        return itemClient.patchItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam String text,
                                         @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(required = false, defaultValue = "20") @Positive int size) {
        return itemClient.search(userId, text, from, size);
    }
}

