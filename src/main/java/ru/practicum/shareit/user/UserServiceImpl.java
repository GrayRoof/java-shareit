package ru.practicum.shareit.user;

import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * @param id
     * @return
     */
    @Override
    public User get(long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    /**
     * @return
     */
    @Override
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * @param userDto
     * @return
     */
    @Override
    public User add(UserDto userDto) {
        return userRepository.save(modelMapper.map(userDto, User.class));
    }

    /**
     * @param userDto
     * @return
     */
    @Override
    public User patch(UserDto userDto, long id) throws NotFoundException {
        User user = modelMapper.map(get(id), User.class);
        modelMapper.map(userDto, user);
        return userRepository.save(user);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }
}
