package ru.practicum.shareit.user;

import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    User get(long id) throws NotFoundException;

    Collection<User> getAll();

    User add(User user);

    User patch(User user) throws NotFoundException;

    Boolean delete(long id);
}
