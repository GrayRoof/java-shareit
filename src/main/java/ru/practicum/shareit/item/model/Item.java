package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "items")
@ToString
@Data
@RequiredArgsConstructor
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
    @Column(name = "OwnerID")
    private Long owner;
    @Column(name = "RequestID")
    private Long request;

    public Item(long id, String name, String description, Boolean available, long owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
