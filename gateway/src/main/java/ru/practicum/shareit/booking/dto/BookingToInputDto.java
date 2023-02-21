package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingToInputDto {
    @NotNull
    Long itemId;
    @FutureOrPresent
    LocalDateTime start;
    @FutureOrPresent
    LocalDateTime end;
}
