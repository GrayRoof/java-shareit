package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable long id) {

        return userService.get(id);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {

        return userService.getAll();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {

        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable Long userId) {
        return userService.patch(userDto, userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {

        userService.delete(id);
    }

}
