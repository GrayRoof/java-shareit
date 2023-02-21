package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void toUserDto() {
        User user = new User(1L, "First", "First@test.test");
        UserDto expected = new UserDto(1L, "First", "First@test.test");
        UserDto actual = UserMapper.toUserDto(user);
        assertEquals(expected, actual);
    }

    @Test
    void toUser() {
        UserDto userDto = new UserDto(1L, "First", "First@test.test");
        User expected = new User(1L, "First", "First@test.test");
        User actual = UserMapper.toUser(userDto);
        assertEquals(expected, actual);
    }
}