package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.Exception.NotAvailableException;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingNestedDto;
import ru.practicum.shareit.booking.dto.BookingToInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private static final long WRONG_ID = Long.MAX_VALUE;

    UserDto ownerDto;
    UserDto otherUserDto;

    ItemAllFieldsDto firstItemDto;
    ItemAllFieldsDto secondItemDto;


    @BeforeEach
    void setUp() {
        entityManager.createQuery("DELETE FROM Booking").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createQuery("DELETE FROM Item").executeUpdate();
        if (userService.getAll().isEmpty()) {
            ownerDto = userService.add(UserMapper.toUserDto(new User(1L, "Owner", "Owner@test.test")));
            otherUserDto = userService.add(UserMapper.toUserDto(new User(2L, "Other", "Other@test.test")));
        }
        if (itemService.getAllByUserId(ownerDto.getId(), 0, 20).isEmpty()) {
            ItemAllFieldsDto dtoToInput = new ItemAllFieldsDto();
            dtoToInput.setName("First Item");
            dtoToInput.setDescription("First Item Description");
            dtoToInput.setAvailable(true);
            firstItemDto = itemService.add(dtoToInput, ownerDto.getId());
            dtoToInput.setName("Second Item");
            dtoToInput.setDescription("Second Item Description");
            dtoToInput.setAvailable(false);
            secondItemDto = itemService.add(dtoToInput, ownerDto.getId());
        }
    }

    @Test
    void shouldReturnBookingDto() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        Booking saved = bookingRepository.save(booking);
        BookingDto expected = BookingMapper.toBookingDto(saved);
        BookingDto actual = bookingService.get(otherUserDto.getId(), saved.getId());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenGetWithWrongId() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        assertThrows(NotFoundException.class, () -> bookingService.get(otherUserDto.getId(), WRONG_ID));
    }

    @Test
    void shouldReturnLastForItem() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        Booking saved = bookingRepository.save(booking);
        BookingNestedDto expected = BookingMapper.toBookingNestedDto(saved);
        BookingNestedDto actual = bookingService.getLastForItem(firstItemDto.getId());
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnNextForItem() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(8));
        booking.setStatus(BookingStatus.APPROVED);
        Booking saved = bookingRepository.save(booking);
        BookingNestedDto expected = BookingMapper.toBookingNestedDto(saved);
        BookingNestedDto actual = bookingService.getNextForItem(firstItemDto.getId());
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnCreated() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(8));
        booking.setStatus(BookingStatus.APPROVED);
        Booking saved = bookingRepository.save(booking);
        Collection<BookingDto> expected = List.of(BookingMapper.toBookingDto(saved));
        Collection<BookingDto> actual = bookingService.getCreated(otherUserDto.getId(), "FUTURE", 0, 20);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnForOwnedItems() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(8));
        booking.setStatus(BookingStatus.APPROVED);
        Booking saved = bookingRepository.save(booking);
        Collection<BookingDto> expected = List.of(BookingMapper.toBookingDto(saved));
        Collection<BookingDto> actual = bookingService.getForOwnedItems(ownerDto.getId(), "FUTURE", 0, 20);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnFinishedCount() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(3));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        int actual = bookingService.getFinishedCount(otherUserDto.getId(), firstItemDto.getId());
        assertEquals(1, actual);
    }

    @Test
    void shouldCreate() {
        BookingToInputDto toInputDto = new BookingToInputDto();
        toInputDto.setItemId(firstItemDto.getId());
        toInputDto.setStart(LocalDateTime.now().plusDays(1));
        toInputDto.setEnd(LocalDateTime.now().plusDays(2));
        Item item = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        item.setId(firstItemDto.getId());
        Booking newBooking = BookingMapper.toBooking(
                toInputDto,
                UserMapper.toUser(otherUserDto),
                item);
        BookingDto actual = bookingService.create(otherUserDto.getId(), toInputDto);
        BookingDto expected = BookingMapper.toBookingDto(newBooking);
        expected.setId(actual.getId());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenOwnerTryToCreate() {
        BookingToInputDto toInputDto = new BookingToInputDto();
        toInputDto.setItemId(firstItemDto.getId());
        toInputDto.setStart(LocalDateTime.now().plusDays(1));
        toInputDto.setEnd(LocalDateTime.now().plusDays(2));
        assertThrows(NotFoundException.class, () -> bookingService.create(ownerDto.getId(), toInputDto));
    }

    @Test
    void shouldThrowExceptionWhenTryCreateForNotAvailableItem() {
        BookingToInputDto toInputDto = new BookingToInputDto();
        toInputDto.setItemId(secondItemDto.getId());
        toInputDto.setStart(LocalDateTime.now().plusDays(1));
        toInputDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotAvailableException.class, () -> bookingService.create(otherUserDto.getId(), toInputDto));
    }

    @Test
    void shouldSetApproved() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(3));
        booking.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);
        BookingDto expected = BookingMapper.toBookingDto(saved);
        expected.setStatus(BookingStatus.APPROVED);
        BookingDto actual = bookingService.setApproved(ownerDto.getId(), saved.getId(), true);
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenTrySetApprovedByNotOwnerOfItem() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(3));
        booking.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);
        assertThrows(NotFoundException.class, () -> bookingService
                .setApproved(otherUserDto.getId(), saved.getId(), true));
    }

    @Test
    void shouldThrowExceptionWhenTrySetApprovedToApprovedBooking() {
        Booking booking = new Booking();
        Item bookingItem = ItemMapper.toItem(firstItemDto, UserMapper.toUser(ownerDto));
        bookingItem.setId(firstItemDto.getId());
        booking.setItem(bookingItem);
        booking.setBooker(UserMapper.toUser(otherUserDto));
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(3));
        booking.setStatus(BookingStatus.APPROVED);
        Booking saved = bookingRepository.save(booking);
        assertThrows(NotAvailableException.class, () -> bookingService
                .setApproved(ownerDto.getId(), saved.getId(), true));
    }
}