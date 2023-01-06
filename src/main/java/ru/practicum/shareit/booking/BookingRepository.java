package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    default Booking get(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Бронирование с идентификатором #" + id
                + " не зарегистрировано!"));
    }

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    Collection<Booking> getAll(long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    Collection<Booking> getAllByStatus(long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start <= ?2 AND b.end >= ?2 ORDER BY b.start DESC")
    Collection<Booking> getAllCurrent(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    Collection<Booking> getAllPast(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    Collection<Booking> getAllFuture(long bookerId, LocalDateTime now);


    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    Collection<Booking> getAllForOwner(long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    Collection<Booking> getAllByStatusForOwner(long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start <= ?2 AND b.end >= ?2 ORDER BY b.start DESC")
    Collection<Booking> getAllCurrentForOwner(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    Collection<Booking> getAllPastForOwner(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    Collection<Booking> getAllFutureForOwner(long bookerId, LocalDateTime now);


    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.end < ?2 ORDER BY b.end DESC")
    Booking getLastForItem(long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.start > ?2 ORDER BY b.start ASC")
    Booking getNextForItem(long itemId, LocalDateTime now);


    @Query(
            "SELECT COUNT (b) FROM Booking b " +
                    "WHERE b.booker.id = ?1 " +
                    "AND b.item.id = ?2 " +
                    "AND b.end < ?3 " +
                    "AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED"
    )
    Integer getFinishedCount(long userId, long itemId, LocalDateTime now);
}
