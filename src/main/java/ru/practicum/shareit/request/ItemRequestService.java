package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestToInputDto;

import java.util.Collection;

public interface ItemRequestService {

    /**
     * Возвращает DTO Вещи по идентификатору
     * @param id идентификатор Запроса
     * @param userId идентификатор пользователя
     * @return ItemRequestDto
     */
    ItemRequestDto get(long id, long userId);

    /**
     * Возвращает DTO Вещи по идентификатору
     * @param userId идентификатор пользователя
     * @param from начальная позиция
     * @param size количество записей в выдаче
     * @return коллекцию ItemRequestDto
     */
    Collection<ItemRequestDto> getAll(long userId, int from, int size);

    /**
     * Возвращает DTO Вещи по идентификатору
     * @param userId идентификатор пользователя
     * @return коллекцию ItemRequestDto
     */
    Collection<ItemRequestDto> getByUserId(long userId);

    /**
     * Возвращает DTO Вещи по идентификатору
     * @param itemRequestDto идентификатор вещи
     * @param userId идентификатор пользователя
     * @return ItemRequestDto
     */
    ItemRequestDto add(ItemRequestToInputDto itemRequestDto, long userId);

}
