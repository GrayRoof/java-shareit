package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentToInputDto;
import ru.practicum.shareit.item.dto.ItemToInputDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {

        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemAllFieldsDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId) throws NotFoundException {
        log.info("ITEM получен запрос GET " + itemId);
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public Collection<ItemAllFieldsDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("ITEM получен запрос GET ALL");
        return itemService.getAllByUserId(userId, from, size);
    }

    @PostMapping
    public ItemAllFieldsDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody ItemAllFieldsDto itemAllFieldsDto) throws NotFoundException {
        log.info("ITEM получен запрос POST userId =" + userId + "тело запроса: " + itemAllFieldsDto);
        return itemService.add(itemAllFieldsDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentToInputDto commentToInputDto
    ) {
        log.info("ITEM COMMENT получен запрос POST userId = " + userId
                + " itemId = " + itemId + " тело запроса: " + commentToInputDto);
        return itemService.addComment(userId, itemId, commentToInputDto);
    }

    @PatchMapping("/{itemId}")
    public ItemAllFieldsDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId,
                                      @Valid @RequestBody ItemToInputDto itemToInputDto) throws NotFoundException {
        log.info("ITEM получен запрос PATCH userId = " + userId
                + " itemId = " + itemId + " тело запроса " + itemToInputDto);
        return itemService.patch(itemToInputDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemAllFieldsDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam String text,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("ITEM получен запрос GET userId = " + userId + " search = " + text);
        return itemService.search(text, userId, from, size);
    }

}
