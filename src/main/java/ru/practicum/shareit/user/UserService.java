package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto get(long id);

    Collection<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto patch(UserDto userDto, long id);

    Boolean delete(long id);
}
