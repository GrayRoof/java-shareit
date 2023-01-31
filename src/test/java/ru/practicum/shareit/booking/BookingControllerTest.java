package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Exception.NotAvailableException;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.NotValidException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingToInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private static final long WRONG_ID = Long.MAX_VALUE;

    User owner;
    User requester;
    ItemRequest firstItemRequest;
    Item firstItem;
    Booking booking;
    BookingDto bookingDto;
    BookingToInputDto bookingToInputDto;


    @BeforeEach
    void setUp() {

        owner = new User(1L, "First", "First@test.test");
        requester = new User(2L, "Second", "Second@test.test");
        firstItemRequest = new ItemRequest(1L, "First", requester, LocalDateTime.now());
        firstItem = new Item(1L, "FirstItem", "FirstItem description",
                true, owner, firstItemRequest.getId());

        bookingToInputDto = new BookingToInputDto();
        bookingToInputDto.setItemId(firstItem.getId());
        bookingToInputDto.setStart(LocalDateTime.now().plusDays(1));
        bookingToInputDto.setEnd(LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(firstItem);
        booking.setBooker(requester);
        booking.setStatus(BookingStatus.APPROVED);

        bookingDto = BookingMapper.toBookingDto(booking);


    }

    @Test
    void shouldCallGet() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenReturn(bookingDto);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void shouldReturn404WhenThrowNotFoundExceptionGetWithWrongUserId() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("NotFoundException"));
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", WRONG_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("NotFoundException")));
    }

    @Test
    void shouldReturn404WhenThrowNotFoundExceptionGetWithWrongId() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("NotFoundException"));
        mvc.perform(get("/bookings/" + WRONG_ID)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCallGetCreated() throws Exception {
        when(bookingService.getCreated(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings")
                    .param("from", "0")
                    .param("size", "2")
                    .param("state", "FUTURE")
                    .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void shouldReturn400WhenThrowNotValidException() throws Exception {
        when(bookingService.getCreated(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new NotValidException("NotValidException"));
        mvc.perform(get("/bookings")
                        .param("from", "-10")
                        .param("size", "2")
                        .param("state", "FUTURE")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("NotValidException")));
    }

    @Test
    void shouldCallGetForOwnedItems() throws Exception {
        when(bookingService.getForOwnedItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "2")
                        .param("state", "FUTURE")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void shouldCreate() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingToInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void shouldReturn400WhenCreateWithNotValidValues() throws Exception {
        ValidationException e = new ConstraintViolationException("ValidationException", new HashSet<>());
        when(bookingService.create(anyLong(), any()))
                .thenThrow(e);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingToInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn4xxWhenThrowNotAvailableException() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(new NotAvailableException("NotAvailableException"));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingToInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("NotAvailableException")));
    }

    @Test
    void shouldCallSetApproved() throws Exception {
        when(bookingService.setApproved(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }
}