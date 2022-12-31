package ru.practicum.shareit.item;

import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    /**
     * Возвращает Вещь по идентификатору
     * @param id идентфикатор Вещи
     * @return объект Item
     */
    Item get(long id) throws NotFoundException;

    /**
     * Возвращает коллекцию Вещей Пользователя
     * @param ownerId идентификатор Пользователя - владельца Вещи
     * @return коллекцию объектов Item
     */
    Collection<Item> getAllByOwnerId(long ownerId);

    /**
     * Реализует добавление Вещи в хранилище
     * @param item объект Вещи
     * @return добавленный объект Item в хранилище
     */
    Item add(Item item);

    /**
     * Реализует обновление полей хранимой Вещи
     * @param item объект Вещи с изменениями
     * @return обновленный объект Item
     */
    Item patch(Item item) throws NotFoundException;

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
     * @return коллекцию объектов Item
     */
    Collection<Item> search(String keyword, long userId);
}
