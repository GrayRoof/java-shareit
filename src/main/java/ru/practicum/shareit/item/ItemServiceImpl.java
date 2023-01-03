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
    public ItemAllFieldsDto get(long id) throws NotFoundException {
        Item item = getEntity(id);
        ItemAllFieldsDto itemAllFieldsDto = ItemMapper.toItemDto(item);
        return itemAllFieldsDto;
    }

    @Override
    public Item getEntity(long id) throws NotFoundException {
        return itemRepository.get(id);
    }

    @Override
    public Collection<ItemAllFieldsDto> getAllByUserId(long userId) {
       return itemRepository.findAllByOwner_IdOrderByIdAsc(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemAllFieldsDto add(ItemAllFieldsDto itemAllFieldsDto, long userId) throws NotFoundException {
        User owner = UserMapper.toUser(userService.get(userId));
        if (Objects.isNull(itemAllFieldsDto.getAvailable())) {
            throw new NotValidException("Поле Доступность не должно быть пустым!");
        }
        Item item = ItemMapper.toItem(itemAllFieldsDto, owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemAllFieldsDto patch(ItemToInputDto itemToInputDto, long itemId, long userId) throws ForbiddenException, NotFoundException {
        Item storedItem = itemRepository.findByIdOrderByIdDesc(itemId);
        if (storedItem.getOwner().getId() != userId) {
            throw new ForbiddenException("Владелец вещи не совпадает с пользователем " + userId + ". " +
                    "Изменить вещь может только владелец!");
        } else {
            if (itemToInputDto.getName() != null && !itemToInputDto.getName().isEmpty()) {
                storedItem.setName(itemToInputDto.getName());
            }
            if (itemToInputDto.getDescription() != null && !itemToInputDto.getDescription().isEmpty()) {
                storedItem.setDescription(itemToInputDto.getDescription());
            }
            storedItem.setAvailable(itemToInputDto.getAvailable());

            Item patchedItem = itemRepository.save(storedItem);
            ItemAllFieldsDto patchedItemAllFieldsDto = ItemMapper.toItemDto(patchedItem);
            log.info("Вещь с идентификатором #{} обновлена. обновленные данные {}", storedItem.getId(), storedItem);
            return patchedItemAllFieldsDto;
        }
    }

    @Override
    public void delete(long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public Collection<ItemAllFieldsDto> search(String text, long userId) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<ItemAllFieldsDto> result = itemRepository.search(text)
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
