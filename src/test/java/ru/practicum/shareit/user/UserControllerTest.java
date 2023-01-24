package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    User user;
    UserDto userToUpdateDto;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "First",
                "First@test.test");
        userDto = UserMapper.toUserDto(user);
        userToUpdateDto = new UserDto(
                1L,
                "Updated",
                "First@test.test");
    }

    @Test
    void shouldCallGetUserById() throws Exception {
        when(userService.get(anyLong()))
                .thenReturn(userDto);
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void shouldCallGetAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userDto));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Long.class));
    }

    @Test
    void shouldCallCreate() throws Exception {
        when(userService.add(any())).thenReturn(userDto);
        mvc.perform(
                    post("/users")
                    .content(mapper.writeValueAsString(userDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));

    }

    @Test
    void shouldCallUpdate() throws Exception {
        when(userService.patch(any(), anyLong())).thenReturn(userToUpdateDto);
        mvc.perform(patch("/users/1")
                    .content(mapper.writeValueAsString(userToUpdateDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userToUpdateDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userToUpdateDto.getName())));
    }

    @Test
    void shouldCallDelete() throws Exception {
        mvc.perform(delete("/users/1")).andExpect(status().isOk());
    }
}