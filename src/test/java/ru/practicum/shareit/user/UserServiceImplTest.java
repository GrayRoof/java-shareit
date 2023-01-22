package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private static final long WRONG_ID = Long.MAX_VALUE;


    @BeforeEach
    void setUp() {
        entityManager.createQuery("DELETE FROM User").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();


    }

    @Test
    void shouldReturnUserById() {
        User localUser = new User(1L, "First", "first@test.test");
        User savedUser = userRepository.save(localUser);
        Assertions.assertEquals(localUser.getName(), userService.get(savedUser.getId()).getName());
    }

    @Test
    void shouldThrowExceptionWhenGetUserByWrongId() {
        assertThrows(NotFoundException.class, () -> userService.get(WRONG_ID));
    }

    @Test
    void shouldReturnWhenGetAll() {
        User localFirstUser = new User(1L, "First", "first@test.test");
        User localSecondUser = new User(2L, "Second", "second@test.test");
        Collection<UserDto> expected = List.of(
                UserMapper.toUserDto(userRepository.save(localFirstUser)),
                UserMapper.toUserDto(userRepository.save(localSecondUser)));
        Collection<UserDto> actual = userService.getAll();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void shouldReturnEmptyCollectionWhenGetAllWithNoData() {
        Collection<UserDto> expected = List.of();
        Collection<UserDto> actual = userService.getAll();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void shouldAddUserWithCorrectData() {
        User localFirstUser = new User(1L, "First", "first@test.test");
        UserDto expected = UserMapper.toUserDto(localFirstUser);
        UserDto actual = userService.add(expected);
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected, userService.get(actual.getId()));
    }

    @Test
    void shouldThrowExceptionsWhenAddUserWithEmptyName() {
        User localFirstUser = new User(1L, "", "first@test.test");
        UserDto emptyNameUserDto = UserMapper.toUserDto(localFirstUser);
        assertThrows(ConstraintViolationException.class, () -> userService.add(emptyNameUserDto));
    }

    @Test
    void shouldThrowExceptionsWhenAddUserWithEmptyEmail() {
        User localFirstUser = new User(1L, "First", "");
        UserDto emptyNameUserDto = UserMapper.toUserDto(localFirstUser);
        assertThrows(ConstraintViolationException.class, () -> userService.add(emptyNameUserDto));
    }

    @Test
    void patch() {
        User localUser = new User(1L, "First", "first@test.test");
        User savedUser = userRepository.save(localUser);
        localUser.setName("Updated");
        UserDto userDtoToUpdate = UserMapper.toUserDto(localUser);

        UserDto actual = userService.patch(userDtoToUpdate, savedUser.getId());
        Assertions.assertEquals(userDtoToUpdate.getName(), actual.getName());
    }

    @Test
    void delete() {
        User localUser = new User(1L, "First", "first@test.test");
        User savedUser = userRepository.save(localUser);
        Assertions.assertEquals(localUser.getName(), userService.get(savedUser.getId()).getName());
        userService.delete(savedUser.getId());
        assertThrows(NotFoundException.class, () -> userService.get(savedUser.getId()));
    }
}