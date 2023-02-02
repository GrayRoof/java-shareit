package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingNestedDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.OffsetPageable;
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
    private final BookingService bookingService;


    @Override
    public ItemAllFieldsDto get(long id, long userId) throws NotFoundException {
        Item item = getEntity(id);
        Collection<Comment> comments = getComments(id);
        ItemAllFieldsDto itemAllFieldsDto = ItemMapper.toItemDto(item, comments);
        if (userId == item.getOwner().getId()) {
            itemAllFieldsDto.setLastBooking(getLastBooking(id));
            itemAllFieldsDto.setNextBooking(getNextBooking(id));
        }
        return itemAllFieldsDto;
    }

    @Override
    public Item getEntity(long id) throws NotFoundException {
        return itemRepository.get(id);
    }

    @Override
    public Collection<ItemAllFieldsDto> getAllByUserId(long userId, int from, int size) {
       return itemRepository.findAllByOwner_IdOrderByIdAsc(userId, OffsetPageable.of(from, size, Sort.unsorted()))
               .stream()
               .map(item -> ItemMapper.toItemDto(item, getComments(item.getId()),
                        getLastBooking(item.getId()), getNextBooking(item.getId())))
               .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemAllFieldsDto> getAllByRequestId(long requestId) {
        return itemRepository.findAllByRequest(requestId).stream()
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
    public ItemAllFieldsDto patch(ItemToInputDto itemToInputDto, long itemId, long userId)
            throws ForbiddenException, NotFoundException {
        Item storedItem = itemRepository.get(itemId);
        if (storedItem.getOwner().getId() != userId) {
            throw new ForbiddenException("Владелец вещи не совпадает с пользователем " + userId + ". " +
                    "Изменить вещь может только владелец!");
        } else {

            Item patchedItem = itemRepository.save(
                    patch(storedItem,
                            itemToInputDto.getName(),
                            itemToInputDto.getDescription(),
                            itemToInputDto.getAvailable()
                    )
            );
            ItemAllFieldsDto patchedItemAllFieldsDto = ItemMapper.toItemDto(patchedItem);
            log.info("Вещь с идентификатором #{} обновлена. обновленные данные {}", storedItem.getId(), storedItem);
            return patchedItemAllFieldsDto;
        }
    }

    private Item patch(Item storedItem, String name, String description, Boolean available) {
        if (name != null && !name.isEmpty()) {
            storedItem.setName(name);
        }
        if (description != null && !description.isEmpty()) {
            storedItem.setDescription(description);
        }
        if (available != null) {
            storedItem.setAvailable(available);
        }
        return storedItem;
    }

    @Override
    public void delete(long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public Collection<ItemAllFieldsDto> search(String text, long userId, int from, int size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<ItemAllFieldsDto> result = itemRepository.search(text, OffsetPageable.of(from, size, Sort.unsorted()))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Получен результат поиска {}", result);
        return result;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentToInputDto commentToInputDto) {
        User author = UserMapper.toUser(userService.get(userId));
        Item item = itemRepository.get(itemId);
        if (isBooker(userId, itemId)) {
            Comment newComment = CommentMapper.toComment(commentToInputDto, author, item);
            return CommentMapper.toCommentDto(commentRepository.save(newComment));
        } else {
            throw new NotValidException("Комментарий может оставить только арендатор Вещи!");
        }
    }

    private Collection<Comment> getComments(long itemId) {
       return commentRepository.findAllByItem_IdOrderByCreatedDesc(itemId);
    }

    private BookingNestedDto getLastBooking(long itemId) {
        return bookingService.getLastForItem(itemId);
    }

    private BookingNestedDto getNextBooking(long itemId) {
        return bookingService.getNextForItem(itemId);
    }

    private boolean isBooker(long userId, long itemId) {
        int count = bookingService.getFinishedCount(userId, itemId);
        return count > 0;
    }
}
