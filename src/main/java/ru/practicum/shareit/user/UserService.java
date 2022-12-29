package ru.practicum.shareit.user;

import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User get(long id) throws NotFoundException;

    Collection<User> getAll();

    User add(UserDto userDto);

    User patch(UserDto userDto, long id) throws NotFoundException;

    void delete(long id);
}
