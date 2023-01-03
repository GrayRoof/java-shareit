package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exception.ForbiddenException;
import ru.practicum.shareit.Exception.NotValidException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingKeyWords;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingToInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
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
    private final ItemService itemService;

    @Override
    public BookingDto get(long userId, long bookingId) {
        User currentUser = UserMapper.toUser(userService.get(userId));
        Booking booking = bookingRepository.get(bookingId);
        if (booking.getBooker().equals(currentUser) || booking.getItem().getOwner().equals(currentUser)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ForbiddenException("Операция может быть выполнена только автором бронирования или владельцем вещи");
        }
    }

    @Override
    public Collection<BookingDto> getCreated(long userId, String word) {
        userService.get(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingKeyWords keyWord = parse(word);
        Collection<Booking> found = null;
        switch (keyWord) {
            case ALL:
                found = bookingRepository.getAllForBooker(userId);
                break;
            case WAITING:
                found = bookingRepository.getAllByStatusForBooker(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                found = bookingRepository.getAllByStatusForBooker(userId, BookingStatus.REJECTED);
                break;
            case PAST:
                found = bookingRepository.getAllPastForBooker(userId, now);
                break;
            case FUTURE:
                found = bookingRepository.getAllFutureForBooker(userId, now);
                break;
            case CURRENT:
                found = bookingRepository.getAllCurrentForBooker(userId, now);
                break;
        }
        return found.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getForOwnedItems(long ownerId, String keyWord) {
        return null;
    }

    @Override
    public BookingDto create(long userId, BookingToInputDto bookingToInputDto) {
        User currentUser = UserMapper.toUser(userService.get(userId));
        Item item = itemService.getEntity(bookingToInputDto.getItemId());

        if (userId == item.getOwner().getId()) {
            throw new ForbiddenException("Владелец Вещи не может забронировать Вещь #" + item.getId());
        }

        if (!item.getAvailable()) {
            throw new ForbiddenException("Вещь #" + item.getId() + " недоступна для бронирования");
        }

        Booking newBooking = BookingMapper.toBooking(bookingToInputDto, currentUser, item);

        Booking created = bookingRepository.save(newBooking);
        log.info(
                "The booking of item #{} (owned by user #{}) was successfully created for user #{}",
                item.getId(),
                item.getOwner().getId(),
                userId
        );
        return BookingMapper.toBookingDto(created);
    }

    @Override
    public BookingDto setApproved(long ownerId, long id, boolean approved) {

        return null;
    }

    private BookingKeyWords parse(String word) {
        try {
            return BookingKeyWords.valueOf(word);
        } catch (IllegalArgumentException exception) {
            throw new NotValidException("Не удалось распознать ключевое слово: " + word);
        }
    }
}
