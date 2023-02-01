package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    User owner;
    User requester;
    ItemRequest firstRequest;
    ItemRequest secondRequest;
    Item firstItem;
    Item secondItem;
    Comment comment;
    ItemAllFieldsDto firstItemDto;
    ItemAllFieldsDto secondItemDto;
    CommentDto commentDto;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "Owner@test.test");
        requester = new User(2L, "Requester", "Requester@test.test");
        firstRequest = new ItemRequest(1L, "firstItem", requester, LocalDateTime.now());
        secondRequest = new ItemRequest(2L, "secondItem", requester, LocalDateTime.now());
        firstItem = new Item(1L, "FirstItem", "FirstItem 1",
                true, owner, firstRequest.getId());
        secondItem = new Item(2L, "SecondItem", "SecondItem 2",
                true, owner, secondRequest.getId());
        comment = new Comment();
        comment.setId(1L);
        comment.setItem(firstItem);
        comment.setText("Comment about firstItem");
        comment.setAuthor(requester);
        comment.setCreated(LocalDateTime.now());

        firstItemDto = ItemMapper.toItemDto(firstItem);
        secondItemDto = ItemMapper.toItemDto(secondItem);
        commentDto = CommentMapper.toCommentDto(comment);
    }

    @Test
    void shouldCallGetById() throws Exception {
        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(firstItemDto);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstItemDto.getName())))
                .andExpect(jsonPath("$.description", is(firstItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(firstItemDto.getAvailable())));
    }

    @Test
    void shouldCallGetAll() throws Exception {
        when(itemService.getAllByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(firstItemDto, secondItemDto));
        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2), Integer.class));
    }

    @Test
    void shouldCallAddItem() throws Exception {
        when(itemService.add(any(), anyLong()))
                .thenReturn(firstItemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(firstItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstItemDto.getName())))
                .andExpect(jsonPath("$.description", is(firstItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(firstItemDto.getAvailable())));

    }

    @Test
    void shouldCallAddComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthor().getName())));
    }

    @Test
    void shouldCallPatchItem() throws Exception {
        when(itemService.patch(any(), anyLong(), anyLong()))
                .thenReturn(secondItemDto);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(secondItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(secondItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(secondItemDto.getName())))
                .andExpect(jsonPath("$.description", is(secondItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(secondItemDto.getAvailable())));
    }

    @Test
    void shouldReturn403WhenPatchItemByOtherUser() throws Exception {
        when(itemService.patch(any(), anyLong(), anyLong()))
                .thenThrow(new ForbiddenException("ForbiddenException"));
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(secondItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("ForbiddenException")));
    }

    @Test
    void shouldCallSearch() throws Exception {
        when(itemService.search(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(secondItemDto));
        mvc.perform(get("/items/search")
                        .param("text", "secondItem")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(secondItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(secondItemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(secondItemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(secondItemDto.getAvailable())));
    }
}