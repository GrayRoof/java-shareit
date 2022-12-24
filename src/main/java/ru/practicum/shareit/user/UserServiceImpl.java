package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    /**
     * @param id
     * @return
     */
    @Override
    public UserDto get(long id) {
        UserDto userDto = UserMapper.toUserDto(userStorage.get(id));
        return userDto;
    }

    /**
     * @return
     */
    @Override
    public Collection<UserDto> getAll() {
        Collection<UserDto> userDtos = userStorage.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        return userDtos;
    }

    /**
     * @param userDto
     * @return
     */
    @Override
    public UserDto add(UserDto userDto) {
        User user = userStorage.add(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    /**
     * @param userDto
     * @return
     */
    @Override
    public UserDto patch(UserDto userDto, long id) {
        userDto.setId(id);
        UserDto patched = UserMapper.toUserDto(userStorage.patch(UserMapper.toUser(userDto)));
        return patched;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Boolean delete(long id) {
        return userStorage.delete(id);
    }
}
