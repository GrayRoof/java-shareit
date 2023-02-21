package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ShareItTests {

	@Autowired
	private UserController userController;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
		assertThat(userService).isNotNull();
		assertThat(userRepository).isNotNull();
	}

}
