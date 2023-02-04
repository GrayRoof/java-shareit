package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.OffsetPageable;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto get(long userId, long bookingId) {
        User currentUser = UserMapper.toUser(userService.get(userId));
        Booking booking = bookingRepository.get(bookingId);
        if (booking.getBooker().equals(currentUser) || booking.getItem().getOwner().equals(currentUser)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Операция может быть выполнена только автором бронирования или владельцем вещи");
        }
    }

    @Override
    public BookingNestedDto getLastForItem(long itemId) {
        return BookingMapper.toBookingNestedDto(bookingRepository
                .findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now()));
    }

    @Override
    public BookingNestedDto getNextForItem(long itemId) {
        return BookingMapper.toBookingNestedDto(bookingRepository
                .findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now()));
    }

    @Override
    public Collection<BookingDto> getCreated(long userId, String word, int from, int size) {
        userService.get(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingKeyWords keyWord = parse(word);
        Page<Booking> found = null;
        switch (keyWord) {
            case ALL:
                found = bookingRepository.getAll(userId, OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case WAITING:
                found = bookingRepository.getAllByStatus(userId, BookingStatus.WAITING,
                        OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case REJECTED:
                found = bookingRepository.getAllByStatus(userId, BookingStatus.REJECTED,
                        OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case PAST:
                found = bookingRepository.getAllPast(userId, now, OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case FUTURE:
                found = bookingRepository.getAllFuture(userId, now, OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case CURRENT:
                found = bookingRepository.getAllCurrent(userId, now, OffsetPageable.of(from, size, Sort.unsorted()));
                break;
        }
        return found.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getForOwnedItems(long ownerId, String word, int from, int size) {
        userService.get(ownerId);
        LocalDateTime now = LocalDateTime.now();
        BookingKeyWords keyWord = parse(word);
        Page<Booking> found = null;
        switch (keyWord) {
            case ALL:
                found = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case WAITING:
                found = bookingRepository.getAllByStatusForOwner(ownerId, BookingStatus.WAITING,
                        OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case REJECTED:
                found = bookingRepository.getAllByStatusForOwner(ownerId, BookingStatus.REJECTED,
                        OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case PAST:
                found = bookingRepository.getAllPastForOwner(ownerId, now,
                        OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case FUTURE:
                found = bookingRepository.getAllFutureForOwner(ownerId, now,
                        OffsetPageable.of(from, size, Sort.unsorted()));
                break;
            case CURRENT:
                found = bookingRepository.getAllCurrentForOwner(ownerId, now,
                        OffsetPageable.of(from, size, Sort.unsorted()));
                break;
        }
        return found.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public int getFinishedCount(long userId, long itemId) {
        return bookingRepository.getFinishedCount(userId, itemId, LocalDateTime.now());
    }


    @Override
    public BookingDto create(long userId, BookingToInputDto bookingToInputDto) {
        User currentUser = UserMapper.toUser(userService.get(userId));
        Item item = itemRepository.get(bookingToInputDto.getItemId());

        if (userId == item.getOwner().getId()) {
            throw new NotFoundException("Владелец Вещи не может забронировать Вещь #" + item.getId());
        }

        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь #" + item.getId() + " недоступна для бронирования");
        }

        Booking newBooking = BookingMapper.toBooking(bookingToInputDto, currentUser, item);
        validateDates(newBooking.getStart(), newBooking.getEnd());
        Booking created = bookingRepository.save(newBooking);
        log.info("Бронирование Вещи #{} для пользователя #{} было успешно создано. Владелец: #{}",
                item.getId(), userId, item.getOwner().getId());
        return BookingMapper.toBookingDto(created);
    }

    @Override
    public BookingDto setApproved(long ownerId, long id, boolean approved) {
        userService.get(ownerId);
        Booking booking = bookingRepository.get(id);

        if (ownerId != booking.getItem().getOwner().getId()) {
            throw new NotFoundException("Только владелец Вещи может одобрить Бронирование #" + id);
        }

        BookingStatus currentStatus = booking.getStatus();
        if (currentStatus == BookingStatus.APPROVED) {
            throw new NotAvailableException("Бронирование #" + id + " уже одобрено");
        }

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        Booking patchedWithStatus = bookingRepository.save(booking);
        log.info("Статус Бронирования #{} изменен с {} на {}", id, currentStatus, newStatus);
        return BookingMapper.toBookingDto(patchedWithStatus);
    }

    private BookingKeyWords parse(String word) {
        try {
            return BookingKeyWords.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new NotValidException("Unknown state: " + word);
        }
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new NotValidException("Дата Начала Бронирования не может быть раньше Даты окончания бронирования!");
        }
    }
}
