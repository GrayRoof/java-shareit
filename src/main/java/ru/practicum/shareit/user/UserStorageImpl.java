package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.Exception.DuplicateEmailException;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorageImpl implements UserStorage {

    private int increment = 0;
    private final Map<Long, User> users = new HashMap<>();

    /**
     * @param id
     * @return
     */
    @Override
    public User get(long id) {
        validateId(id);
        return users.get(id);
    }

    /**
     * @return
     */
    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    /**
     * @param user
     * @return
     */
    @Override
    public User add(User user) {
        validateEmail(user);
        user.setId(++increment);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    /**
     * @param user
     * @return
     */
    @Override
    public User patch(User user) {
        validateId(user.getId());
        validateEmail(user);
        User patchedUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isEmpty()) {
            patchedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            patchedUser.setEmail(user.getEmail());
        }
        users.put(patchedUser.getId(), patchedUser);
        return users.get(patchedUser.getId());
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Boolean delete(long id) {
        users.remove(id);
        return !users.containsKey(id);
    }

    private void validateId(long id) {
        if(id != 0 && !users.containsKey(id)) {
            throw new NotFoundException("Пользователь с идентификатором " +
                    id + " не зарегистрирован!");
        }
    }
    private void validateEmail(User user) {
        if (users.values()
                .stream()
                .anyMatch(
                        stored -> stored.getEmail().equalsIgnoreCase(user.getEmail())
                        && stored.getId() != user.getId()
                )
        ) {
            throw new DuplicateEmailException("Пользователь с таким адресом Эл. почты " +
                    user.getEmail() + " уже существует!");
        }
    }
}
