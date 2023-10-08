package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class AppApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	private Faker faker = new Faker();

	@Autowired
	private ObjectMapper mapper;

	private User testUser;

	@BeforeEach
	void prepareData() {
		userRepository.deleteAll();
		User newUser = Instancio.of(User.class)
						.ignore(Select.field(User::getId))
						.ignore(Select.field(User::getCreatedAt))
						.supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
						.create();

		String userName = newUser.getFirstName();

		userRepository.save(newUser);

		testUser = userRepository.findAll().get(0);

	}

	@Test
	void getUserByIdTest() throws Exception {

		MockHttpServletRequestBuilder request = get("/api/users/" + testUser.getId());
		mockMvc.perform(request).andExpect(status().isOk());
	}

	@Test
	void getAllUsersTest() throws Exception {
		MockHttpServletRequestBuilder request = get("/api/users");
		MvcResult result = mockMvc.perform(request)
						.andExpect(status().isOk())
						.andReturn();

		String response = result.getResponse().getContentAsString();
		assertThat(response).contains(testUser.getFirstName());
	}

	@Test
	void postNewUserTest() throws Exception {

		String newUserDummy = Files.readString(Path.of("src/test/java/resources/testDummy/newUserDummy"));

		MockHttpServletRequestBuilder request = post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(newUserDummy);

		mockMvc.perform(request)
						.andExpect(status().isOk());

		Optional<User> newUser = userRepository.findOneByFirstName("Ivan");
		assertThat(newUser.isEmpty()).isFalse();
	}

	@Test
	void putUserUpdateTest() throws Exception {
		long existingUserId = testUser.getId();

		String content = Files.readString(Path.of("src/test/java/resources/testDummy/updateUserDummy"));
		MockHttpServletRequestBuilder request = put("/api/users/" + existingUserId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(content);

		mockMvc.perform(request)
						.andExpect(status().isOk());

		User modifiedUser = userRepository.getById(existingUserId);
		String email = modifiedUser.getEmail();
//		assertThat(modifiedUser.getEmail()).isEqualTo("updated@yahoo.com");
	}

	@Test
	void deleteExistingUserTest() throws Exception {
		long existingUserId = testUser.getId();

		MockHttpServletRequestBuilder request = delete("/api/users/" + existingUserId);

		mockMvc.perform(request).andExpect(status().isOk());

		assertThat(userRepository.findById(existingUserId).isEmpty()).isTrue();
	}

}
