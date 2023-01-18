package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exception.NotValidException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public ItemRequestDto get(long id, long userId) {
        userService.get(userId);
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.get(id));

        return requestDto;
    }

    @Override
    public Collection<ItemRequestDto> getAll(long userId, int from, int size) {
        if (size < 1 || from < 0) {
            throw new NotValidException("границы");
        }

        return null;
    }

    @Override
    public Collection<ItemRequestDto> getByUserId(long userId) {
        return null;
    }

    @Override
    public ItemRequestDto add(ItemRequestDto itemRequestDto, long userId) {
        return null;
    }
}
