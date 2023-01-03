package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exception.ForbiddenException;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.NotValidException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    private final UserService userService;


    @Override
    public ItemToReturnDto get(long id) throws NotFoundException {
        Item item = getEntity(id);
        ItemToReturnDto itemToReturnDto = ItemMapper.toItemDto(item);
        return itemToReturnDto;
    }

    @Override
    public Item getEntity(long id) throws NotFoundException {
        return itemRepository.get(id);
    }

    @Override
    public Collection<ItemToReturnDto> getAllByUserId(long userId) {
       return itemRepository.findAllByOwnerOrderByIdAsc(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemToReturnDto add(ItemToInputDto itemToInputDto, long userId) throws NotFoundException {
        User owner = UserMapper.toUser(userService.get(userId));
        if (Objects.isNull(itemToInputDto.getAvailable())) {
            throw new NotValidException("Поле Доступность не должно быть пустым!");
        }
        Item item = ItemMapper.toItem(itemToInputDto, owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemToReturnDto patch(ItemToInputDto itemToInputDto, long itemId, long userId) throws ForbiddenException, NotFoundException {
        Item storedItem = itemRepository.findByIdOrderByIdDesc(itemId);
        if (storedItem.getOwner().getId() != userId) {
            throw new ForbiddenException("Владелец вещи не совпадает с пользователем " + userId + ". " +
                    "Изменить вещь может только владелец!");
        } else {
            storedItem.setName(itemToInputDto.getName());
            storedItem.setDescription(itemToInputDto.getDescription());
            storedItem.setAvailable(itemToInputDto.getAvailable());
            Item patchedItem = itemRepository.save(storedItem);
            ItemToReturnDto patchedItemToReturnDto = ItemMapper.toItemDto(patchedItem);
            log.info("Вещь с идентификатором #{} обновлена. обновленные данные {}", storedItem.getId(), storedItem);
            return patchedItemToReturnDto;
        }
    }

    @Override
    public void delete(long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public Collection<ItemToReturnDto> search(String text, long userId) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<ItemToReturnDto> result = itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentToInputDto commentToInputDto) {
        User author = UserMapper.toUser(userService.get(userId));
        Item item = itemRepository.get(itemId);
        Comment newComment = CommentMapper.toComment(commentToInputDto, author, item);
        return CommentMapper.toCommentDto(commentRepository.save(newComment));
    }
}
