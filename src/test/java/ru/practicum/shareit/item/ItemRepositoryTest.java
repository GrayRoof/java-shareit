package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.OffsetPageable;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class ItemRepositoryTest {

    private static final long WRONG_ID = Long.MAX_VALUE;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    User owner;
    User otherUser;
    Item firstItem;

    @BeforeEach
    void setUp() {
        if (userRepository.findAll().isEmpty()) {
            owner = userRepository.save(new User(null, "Owner", "Owner@test.test"));
            otherUser = userRepository.save(new User(null, "Other", "Other@test.test"));
        }
        if (itemRepository.findAll().isEmpty()) {
            firstItem = itemRepository.save(new Item(null, "First Item",
                    "First Item description", true, owner, null));
        }
    }

    @Test
    void shouldReturnItemById() {
        assertEquals(firstItem, itemRepository.get(firstItem.getId()));
    }

    @Test
    void shouldThrowExceptionWhenGetByWrongId() {
        assertThrows(NotFoundException.class, () -> itemRepository.get(WRONG_ID));
    }

    @Test
    void shouldSearchByText() {
        Page<Item> actual = itemRepository.search("first", OffsetPageable.of(0, 20, Sort.unsorted()));
        assertIterableEquals(List.of(firstItem), actual);
    }

    @Test
    void shouldFindAllByOwnerIdOrderByIdAsc() {
        Page<Item> actual = itemRepository.findAllByOwner_IdOrderByIdAsc(
                owner.getId(),
                OffsetPageable.of(0, 20, Sort.unsorted()));
        assertIterableEquals(List.of(firstItem), actual);
    }

    @Test
    void shouldReturnEmptyListWhenFindAllByWrongRequestId() {
        Collection<Item> actual = itemRepository.findAllByRequest(WRONG_ID);
        assertEquals(new ArrayList<>(), actual);
    }
}