package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    default ItemRequest get(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Запрос с идентификатором #" + id
                + " не зарегистрирован!"));
    }

    Collection<ItemRequest> findAllByRequester_IdOrderByCreatedAsc(Long userId);

    Page<ItemRequest> findAll(Pageable pageable);

    Page<ItemRequest> findAllByRequester_IdNot(Long userId, Pageable pageable);
}
