package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.Exception.ForbiddenException;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.NotValidException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private static final long WRONG_ID = Long.MAX_VALUE;

    UserDto owner;
    UserDto otherUser;

    @BeforeEach
    void setUpItems() {
        entityManager.createQuery("DELETE FROM Item").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        if (userService.getAll().isEmpty()) {
            owner = userService.add(UserMapper.toUserDto(new User(1L, "Owner", "Owner@test.test")));
            otherUser = userService.add(UserMapper.toUserDto(new User(2L, "Other", "Other@test.test")));
        }
    }

    @Test
    void shouldReturnItemForOtherUser() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        Item savedItem = itemRepository.save(newItem);
        ItemAllFieldsDto expected = ItemMapper.toItemDto(savedItem,
                new ArrayList<>());
        assertEquals(expected, itemService.get(savedItem.getId(), otherUser.getId()));
    }

    @Test
    void shouldThrowExceptionWhenGetByWrongId() {
        assertThrows(NotFoundException.class, () -> itemService.get(WRONG_ID, otherUser.getId()));
    }

    @Test
    void shouldReturnItemForOwner() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        Item savedItem = itemRepository.save(newItem);
        ItemAllFieldsDto expected = ItemMapper.toItemDto(savedItem,
                new ArrayList<>());
        expected.setLastBooking(BookingMapper.toBookingNestedDto(bookingRepository
                .getLastForItem(savedItem.getId(), LocalDateTime.now())));
        expected.setNextBooking(BookingMapper.toBookingNestedDto(bookingRepository
                .getNextForItem(savedItem.getId(), LocalDateTime.now())));
        assertEquals(expected, itemService.get(savedItem.getId(), owner.getId()));
    }

    @Test
    void shouldGetEntity() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        Item expected = itemRepository.save(newItem);
        Item actual = itemService.getEntity(expected.getId());
        assertEquals(expected, actual);
    }

    @Test
    void shouldGetAllByUserId() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        ItemAllFieldsDto saved = ItemMapper.toItemDto(itemRepository.save(newItem), new ArrayList<>());
        Collection<ItemAllFieldsDto> expected = List.of(saved);
        Collection<ItemAllFieldsDto> actual = itemService.getAllByUserId(owner.getId(), 0, 2);
        assertEquals(expected, actual);
    }

    @Test
    void shouldGetAllByRequestId() {
        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("First item");
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequester(UserMapper.toUser(otherUser));
        ItemRequest savedRequest = itemRequestRepository.save(newRequest);
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), savedRequest.getId());
        ItemAllFieldsDto saved = ItemMapper.toItemDto(itemRepository.save(newItem));
        Collection<ItemAllFieldsDto> expected = List.of(saved);
        Collection<ItemAllFieldsDto> actual = itemService.getAllByRequestId(savedRequest.getId());
        assertEquals(expected, actual);
    }

    @Test
    void shouldAdd() {
        ItemAllFieldsDto newItemDto = new ItemAllFieldsDto();
        newItemDto.setName("NewItem");
        newItemDto.setDescription("New Item description");
        newItemDto.setAvailable(true);

        ItemAllFieldsDto added = itemService.add(newItemDto, owner.getId());
        added.setComments(new ArrayList<>());
        assertEquals(added.getName(), newItemDto.getName());
        ItemAllFieldsDto actual = itemService.get(added.getId(), otherUser.getId());
        assertEquals(added, actual);
    }

    @Test
    void shouldThrowExceptionWhenTryToAddItemWithoutAvailable() {
        ItemAllFieldsDto newItemDto = new ItemAllFieldsDto();
        newItemDto.setName("NewItem");
        newItemDto.setDescription("New Item description");

        assertThrows(NotValidException.class, () -> itemService.add(newItemDto, owner.getId()));
    }

    @Test
    void shouldPatch() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        Item savedItem = itemRepository.save(newItem);
        ItemAllFieldsDto expected = ItemMapper.toItemDto(savedItem);
        expected.setName("Updated");
        ItemToInputDto toUpdate = new ItemToInputDto();
        toUpdate.setName("Updated");
        assertEquals(expected, itemService.patch(toUpdate, savedItem.getId(), owner.getId()));
        assertEquals(expected.getName(), itemService.get(savedItem.getId(), otherUser.getId()).getName());
    }

    @Test
    void shouldThrowExceptionWhenPatchByNotOwner() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        Item savedItem = itemRepository.save(newItem);
        ItemToInputDto toUpdate = new ItemToInputDto();
        toUpdate.setName("Updated");
        assertThrows(ForbiddenException.class, () -> itemService.patch(toUpdate, savedItem.getId(), otherUser.getId()));
    }

    @Test
    void shouldThrowExceptionWhenPatchNotExistingItem() {
        ItemToInputDto toUpdate = new ItemToInputDto();
        toUpdate.setName("Updated");
        assertThrows(NotFoundException.class, () -> itemService.patch(toUpdate, WRONG_ID, otherUser.getId()));
    }

    @Test
    void shouldDelete() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        Item savedItem = itemRepository.save(newItem);
        assertEquals(savedItem.getName(), itemService.get(savedItem.getId(), otherUser.getId()).getName());
        itemService.delete(savedItem.getId());
        assertThrows(NotFoundException.class, () -> itemService.get(savedItem.getId(), owner.getId()));
    }

    @Test
    void shouldSearch() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        Item savedItem = itemRepository.save(newItem);
        Collection<ItemAllFieldsDto> expected = List.of(ItemMapper.toItemDto(savedItem));
        Collection<ItemAllFieldsDto> actual = itemService.search("first", otherUser.getId(), 0,2);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnEmptyListWhenSearchWithEmptySearchWord() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        itemRepository.save(newItem);
        Collection<ItemAllFieldsDto> expected = new ArrayList<>();
        Collection<ItemAllFieldsDto> actual = itemService.search("", otherUser.getId(), 0,2);
        assertEquals(expected, actual);
    }

    @Test
    void shouldAddComment() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        Item savedItem = itemRepository.save(newItem);
        Booking newBooking = new Booking();
        newBooking.setItem(savedItem);
        newBooking.setStatus(BookingStatus.APPROVED);
        newBooking.setBooker(UserMapper.toUser(otherUser));
        newBooking.setStart(LocalDateTime.now().minusDays(2L));
        newBooking.setEnd(LocalDateTime.now().minusDays(1L));
        bookingRepository.save(newBooking);
        CommentToInputDto newComment = new CommentToInputDto();
        newComment.setText("Comment for First Item");
        CommentDto savedCommentDto = itemService.addComment(otherUser.getId(), savedItem.getId(), newComment);
        assertEquals(newComment.getText(), savedCommentDto.getText());
        assertEquals(otherUser.getName(), savedCommentDto.getAuthorName());
    }

    @Test
    void shouldThrowExceptionWhenNotRequesterTryToAddComment() {
        Item newItem = new Item(1L, "First", "First Item description",
                true, UserMapper.toUser(owner), null);
        Item savedItem = itemRepository.save(newItem);
        Booking newBooking = new Booking();
        newBooking.setItem(savedItem);
        newBooking.setStatus(BookingStatus.APPROVED);
        newBooking.setBooker(UserMapper.toUser(otherUser));
        newBooking.setStart(LocalDateTime.now().plusDays(2L));
        newBooking.setEnd(LocalDateTime.now().plusDays(3L));
        bookingRepository.save(newBooking);
        CommentToInputDto newComment = new CommentToInputDto();
        newComment.setText("Comment for First Item");
        assertThrows(NotValidException.class, () -> itemService
                .addComment(otherUser.getId(), savedItem.getId(), newComment));
    }
}