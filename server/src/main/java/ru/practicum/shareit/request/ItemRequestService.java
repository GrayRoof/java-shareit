package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestToInputDto;

import java.util.Collection;

public interface ItemRequestService {

    /**
     * Возвращает DTO Запроса Вещи по идентификатору
     * @param id идентификатор Запроса
     * @param userId идентификатор пользователя
     * @return ItemRequestDto
     */
    ItemRequestDto get(long id, long userId);

    /**
     * Возвращает коллекцию DTO Запросов Вещи
     * @param userId идентификатор пользователя
     * @param from начальная позиция
     * @param size количество записей в выдаче
     * @return коллекцию ItemRequestDto
     */
    Collection<ItemRequestDto> getAll(long userId, int from, int size);

    /**
     * Возвращает коллекцию DTO Запросов Вещи по идентификатору пользователя
     * @param userId идентификатор пользователя - создателя Запроса
     * @return коллекцию ItemRequestDto
     */
    Collection<ItemRequestDto> getByUserId(long userId);

    /**
     * Добавляет Запрос вещи
     * @param itemRequestDto DTO Запроса вещи
     * @param userId идентификатор пользователя - создателя Запроса
     * @return ItemRequestDto
     */
    ItemRequestDto add(ItemRequestToInputDto itemRequestDto, long userId);

}
