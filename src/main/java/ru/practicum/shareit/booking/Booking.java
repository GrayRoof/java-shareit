package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "startdate")
    private LocalDateTime start;
    @Column(name = "enddate")
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "itemID")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "bookerID")
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}

