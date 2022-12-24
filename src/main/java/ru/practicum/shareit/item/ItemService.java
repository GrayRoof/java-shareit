package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    /**
     * Возвращает DTO Вещи по идентификатору
     * @param id идентификатор вещи
     * @return ItemDto
     */
    ItemDto get(long id);

    /**
     * Возвращает коллекцию DTO Вещей Пользователя
     * @param userId идентификатор Пользователя владельца Вещи
     * @return коллекцию ItemDto
     */
    Collection<ItemDto> getAllByUserId(long userId);

    /**
     * Реализует добавление Вещи в хранилище
     * @param itemDto DTO объект Вещи
     * @param ownerId идентификатор Пользователя владельца
     * @return DTO добавленного объекта Item в хранилище
     */
    ItemDto add(ItemDto itemDto, long ownerId);

    /**
     * Реализует обновление полей хранимой Вещи
     * @param itemDto объект Вещи с изменениями
     * @param itemId идентификатор Вещи
     * @param userId идентификатор Пользователя
     * @return DTO обновленного объекта Item
     */
    ItemDto patch(ItemDto itemDto, long itemId, long userId);

    /**
     * Реализует удаление Вещи из хранилища
     * @param id идентификатор удаляемой вещи
     * @return true в случае успешного удаления
     */
    boolean delete(long id);

    /**
     * Реализует поиск Вещей в хранилище по ключевому слову
     * @param keyword ключевое слово для поиска
     * @param userId идентификатор пользователя
     * @return коллекцию DTO объектов Item
     */
    Collection<ItemDto> search(String keyword, long userId);
}
