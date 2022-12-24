package ru.practicum.shareit.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ErrorMessage {
    private Date timestamp;
    private int statusCode;
    private String message;
    private String path;
}