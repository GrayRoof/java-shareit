package ru.practicum.shareit.user;

import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    UserDto get(long id) throws NotFoundException;

    Collection<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto patch(UserDto userDto, long id) throws NotFoundException;

    void delete(long id);
}
