package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    default User get(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Пользователь с идентификатором #" +
                id + " не зарегистрирован!"));
    }
}
