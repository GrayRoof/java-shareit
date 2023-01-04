package ru.practicum.shareit.item;

import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentToInputDto;
import ru.practicum.shareit.item.dto.ItemToInputDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    /**
     * Возвращает DTO Вещи по идентификатору
     * @param id идентификатор вещи
     * @return ItemDto
     */
    ItemAllFieldsDto get(long id, long userId) throws NotFoundException;

    /**
     * Возвращает объект Вещи по идентификатору
     * @param id идентификатор вещи
     * @return Item
     */
    Item getEntity(long id) throws NotFoundException;

    /**
     * Возвращает коллекцию DTO Вещей Пользователя
     * @param userId идентификатор Пользователя владельца Вещи
     * @return коллекцию ItemDto
     */
    Collection<ItemAllFieldsDto> getAllByUserId(long userId);

    /**
     * Реализует добавление Вещи в хранилище
     * @param itemAllFieldsDto DTO объект Вещи
     * @param ownerId идентификатор Пользователя владельца
     * @return DTO добавленного объекта Item в хранилище
     */
    ItemAllFieldsDto add(ItemAllFieldsDto itemAllFieldsDto, long ownerId) throws NotFoundException;

    /**
     * Реализует обновление полей хранимой Вещи
     * @param itemToInputDto объект Вещи с изменениями
     * @param itemId идентификатор Вещи
     * @param userId идентификатор Пользователя
     * @return DTO обновленного объекта Item
     */
    ItemAllFieldsDto patch(ItemToInputDto itemToInputDto, long itemId, long userId) throws NotFoundException;

    /**
     * Реализует удаление Вещи из хранилища
     * @param id идентификатор удаляемой вещи
     * @return true в случае успешного удаления
     */
    void delete(long id);

    /**
     * Реализует поиск Вещей в хранилище по ключевому слову
     * @param keyword ключевое слово для поиска
     * @param userId идентификатор пользователя
     * @return коллекцию DTO объектов Item
     */
    Collection<ItemAllFieldsDto> search(String keyword, long userId);

    /**
     * Реализует добавление комментария к Вещи
     * @param userId идентификатор пользователя. Комментарий может оставить только
     * Владелец Вещи или один из ее арендаторов
     * @param itemId идентификатор Вещи
     * @param commentToInputDto DTO объект комментария
     * @return DTO объекта комментария
     */
    CommentDto addComment(Long userId, Long itemId, CommentToInputDto commentToInputDto);
}
