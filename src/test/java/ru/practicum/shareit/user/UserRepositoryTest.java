package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private static final long WRONG_ID = Long.MAX_VALUE;

    User user;

    @BeforeEach
    void setUp() {
        User localUser = new User(1L, "First", "first@test.test");
        user = userRepository.save(localUser);
    }

    @Test
    void shouldReturnUserById() {
        User actual = userRepository.get(user.getId());
        assertEquals(user, actual);
    }

    @Test
    void shouldThrowExceptionByWrongId() {
        assertThrows(NotFoundException.class, () -> userRepository.get(WRONG_ID));
    }
}