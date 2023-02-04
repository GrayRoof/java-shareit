package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    Long id;
    @NotEmpty
    String name;
    @NotEmpty
    @Email
    String email;
}
