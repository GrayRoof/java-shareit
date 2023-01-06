package ru.practicum.shareit.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Date;

@Getter
@AllArgsConstructor
@Value
public class ErrorMessage {
    Date timestamp;
    int statusCode;
    String error;
    String path;
}