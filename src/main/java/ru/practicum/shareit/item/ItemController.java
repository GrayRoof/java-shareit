package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentToInputDto;
import ru.practicum.shareit.item.dto.ItemToInputDto;
import ru.practicum.shareit.item.dto.ItemToReturnDto;

import javax.validation.Valid;
import java.util.Collection;

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
    public ItemToReturnDto getId(@PathVariable long itemId) throws NotFoundException {
        return itemService.get(itemId);
    }

    @GetMapping
    public Collection<ItemToReturnDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllByUserId(userId);
    }

    @PostMapping
    public ItemToReturnDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @Valid @RequestBody ItemToInputDto itemToReturnDto) throws NotFoundException {
        return itemService.add(itemToReturnDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemToReturnDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long itemId,
                                     @Valid @RequestBody ItemToInputDto itemToReturnDto) throws NotFoundException {
        return itemService.patch(itemToReturnDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemToReturnDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam String text) {
        return itemService.search(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentToInputDto dto
    ) {
        return itemService.addComment(userId, itemId, dto);
    }

}
