package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    User get(long id);

    Collection<User> getAll();

    User add(User user);

    User patch(User user);

    Boolean delete(long id);
}
