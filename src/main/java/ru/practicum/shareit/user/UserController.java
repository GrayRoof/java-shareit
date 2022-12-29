package ru.practicum.shareit.user;

import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable long id) throws NotFoundException {
        return modelMapper.map(userService.get(id), UserDto.class);

    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return modelMapper.map(userService.add(userDto), UserDto.class);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable Long userId) throws NotFoundException {
        return modelMapper.map(userService.patch(userDto, userId), UserDto.class);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }

}
