package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.Exception.NotFoundException;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    default ItemRequest get(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Запрос с идентификатором #" + id
                + " не зарегистрирован!"));
    }

    Collection<ItemRequest> findAllByRequesterIdOrderByCreatedAsc(Long userId);
}
