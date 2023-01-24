package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.pagination.OffsetPageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestToInputDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public ItemRequestDto get(long id, long userId) {
        userService.get(userId);
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.get(id),
                itemService.getAllByRequestId(id));
        log.info(
                "Выдан ItemRequest #'{}' от пользователя #{}. Текст: {}",
                requestDto.getId(),
                userId,
                requestDto.getDescription()
        );
        return requestDto;
    }

    @Override
    public Collection<ItemRequestDto> getAll(long userId, int from, int size) {
        return itemRequestRepository.findAllByRequester_IdNot(userId, OffsetPageable.of(from,size, Sort.unsorted()))
                .stream()
                .map(itemRequest ->
                        ItemRequestMapper.toItemRequestDto(itemRequest,
                                itemService.getAllByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getByUserId(long userId) {
        userService.get(userId);
        return itemRequestRepository.findAllByRequester_IdOrderByCreatedAsc(userId)
                .stream()
                .map(itemRequest ->
                    ItemRequestMapper.toItemRequestDto(itemRequest,
                            itemService.getAllByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto add(ItemRequestToInputDto itemRequestDto, long userId) {
        User requester = UserMapper.toUser(userService.get(userId));
        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription(itemRequestDto.getDescription());
        newRequest.setRequester(requester);
        newRequest.setCreated(LocalDateTime.now());

        ItemRequest created = itemRequestRepository.save(newRequest);
        log.info(
                "Запрос '{}' от Пользователя #{} успешно зарегистрирован с id {}",
                created.getDescription(),
                userId,
                created.getId()
        );
        return ItemRequestMapper.toItemRequestDto(created);
    }
}
