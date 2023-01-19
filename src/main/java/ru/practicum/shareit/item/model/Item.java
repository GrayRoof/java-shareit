package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@ToString
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String description;
    @Column(name = "Isavailable")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "OwnerID")
    private User owner;
    @Column(name = "RequestID")
    private Long request;

}
