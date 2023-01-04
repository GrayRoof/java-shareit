package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNestedDto;
import ru.practicum.shareit.booking.dto.BookingToInputDto;

import java.util.Collection;

public interface BookingService {

    /**
     * Реализует получение Бронирования из хранилища по идентификатору
     * Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование.
     * @param userId идентификатор пользователя
     * @param bookingId идентификатор Бронирования
     * @return коллекцию DTO объектов Booking
     */
    BookingDto get(long userId, long bookingId);

    /**
     * Реализует получение последнего Бронирования из хранилища по идентификатору Вещи
     * @param itemId идентификатор Вещи
     * @return объект Booking
     */
    BookingNestedDto getLastForItem(long itemId);

    /**
     * Реализует получение предстоящего Бронирования из хранилища по идентификатору Вещи
     * @param itemId идентификатор Вещи
     * @return объект Booking
     */
    BookingNestedDto getNextForItem(long itemId);

    /**
     * Реализует получение списка Бронирований из хранилища текущего пользователя
     * @param userId идентификатор пользователя
     * @param keyWord ключевое слово для вывода списка бронирований
     *
     * @return коллекцию DTO объектов Booking
     */
    Collection<BookingDto> getCreated(long userId, String keyWord);

    /**
     * Реализует получение списка бронирований для всех вещей текущего пользователя
     * @param ownerId идентификатор пользователя Владельца Вещи
     * @param keyWord ключевое слово для вывода списка бронирований
     * @return коллекцию DTO объектов Booking
     */
    Collection<BookingDto> getForOwnedItems(long ownerId, String keyWord);

    /**
     * Реализует получение количества завершенных бронирований Вещи, выполненных текущим Пользователем
     * @param itemId идентификатор Вещи
     * @param userId идентификатор Пользователя
     * @return коллекцию DTO объектов Booking
     */
    int getFinishedCount(long userId, long itemId);

    /**
     * Реализует создание Бронирования
     * @param userId идентификатор пользователя
     * @param bookingToInputDto DTO объекта Бронирования
     * @return DTO объект Booking
     */
    BookingDto create(long userId, BookingToInputDto bookingToInputDto);

    /**
     * Реализует подтверждение Бронирования Владельцем Вещи
     * @param ownerId идентификатор пользователя Владельца Вещи
     * @param id идентификатор Бронирования
     * @param approved отметка о подтверждении Бронирования:
     *                 TRUE - Бронирование подтверждено
     *                 FALSE - Бронирование отклонено
     * @return DTO объект Booking
     */
    BookingDto setApproved(long ownerId, long id, boolean approved);
}
