package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.NotValidException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestToInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
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
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private static final long WRONG_ID = Long.MAX_VALUE;

    UserDto owner;
    UserDto otherUser;


    @BeforeEach
    void setUp() {
        entityManager.createQuery("DELETE FROM ItemRequest").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE requests ALTER COLUMN id RESTART WITH 1").executeUpdate();
        if (userService.getAll().isEmpty()) {
            owner = userService.add(UserMapper.toUserDto(new User(1L, "Owner", "Owner@test.test")));
            otherUser = userService.add(UserMapper.toUserDto(new User(2L, "Other", "Other@test.test")));
        }
    }

    @Test
    void shouldReturnItemRequest() {
        ItemRequest request = new ItemRequest(1L, "New Item",
                UserMapper.toUser(otherUser), LocalDateTime.now());
        ItemRequest saved = itemRequestRepository.save(request);
        ItemRequestDto expected = ItemRequestMapper.toItemRequestDto(saved, new ArrayList<>());
        ItemRequestDto actual = itemRequestService.get(saved.getId(), owner.getId());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenTryToGetByWrongUserId() {
        ItemRequest request = new ItemRequest(1L, "New Item",
                UserMapper.toUser(otherUser), LocalDateTime.now());
        ItemRequest saved = itemRequestRepository.save(request);
        assertThrows(NotFoundException.class, () -> itemRequestService.get(saved.getId(), WRONG_ID));
    }

    @Test
    void shouldThrowExceptionWhenTryToGetByWrongId() {
        ItemRequest request = new ItemRequest(1L, "New Item",
                UserMapper.toUser(otherUser), LocalDateTime.now());
        itemRequestRepository.save(request);
        assertThrows(NotFoundException.class, () -> itemRequestService.get(WRONG_ID, owner.getId()));
    }

    @Test
    void shouldReturnAllItemRequests() {
        ItemRequest request = new ItemRequest(1L, "New Item",
                UserMapper.toUser(otherUser), LocalDateTime.now());
        ItemRequest saved = itemRequestRepository.save(request);
        Collection<ItemRequestDto> expected = List.of(ItemRequestMapper.toItemRequestDto(saved, new ArrayList<>()));
        Collection<ItemRequestDto> actual = itemRequestService.getAll(owner.getId(), 0, 2);
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowWhenTryToGetAllItemRequestsWithWrongPageParameters() {
        ItemRequest request = new ItemRequest(1L, "New Item",
                UserMapper.toUser(otherUser), LocalDateTime.now());
        itemRequestRepository.save(request);
        assertThrows(NotValidException.class, () -> itemRequestService.getAll(owner.getId(), -9, 2));
    }

    @Test
    void shouldReturnAllByUserId() {
        ItemRequest request = new ItemRequest(1L, "New Item",
                UserMapper.toUser(otherUser), LocalDateTime.now());
        ItemRequest saved = itemRequestRepository.save(request);
        Collection<ItemRequestDto> expected = List.of(ItemRequestMapper.toItemRequestDto(saved, new ArrayList<>()));
        Collection<ItemRequestDto> actual = itemRequestService.getByUserId(otherUser.getId());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenTryGetAllByWrongUserId() {
        ItemRequest request = new ItemRequest(1L, "New Item",
                UserMapper.toUser(otherUser), LocalDateTime.now());
        itemRequestRepository.save(request);
        assertThrows(NotFoundException.class, () -> itemRequestService.getByUserId(WRONG_ID));
    }

    @Test
    void shouldAdd() {
        ItemRequest request = new ItemRequest(1L, "New Item",
                UserMapper.toUser(otherUser), LocalDateTime.now());
        ItemRequestToInputDto toInputDto = new ItemRequestToInputDto();
        toInputDto.setDescription("New Item");
        ItemRequestDto expected = ItemRequestMapper.toItemRequestDto(request);
        ItemRequestDto actual = itemRequestService.add(toInputDto, otherUser.getId());
        expected.setCreated(actual.getCreated());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenTryToAddByWrongUserId() {
        ItemRequestToInputDto toInputDto = new ItemRequestToInputDto();
        toInputDto.setDescription("New Item");
        assertThrows(NotFoundException.class, () -> itemRequestService.add(toInputDto, WRONG_ID));
    }
}