package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import hexlet.code.app.TestUtils;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.JWTUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private final int TEST_USER_COUNT = 3;

    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final TestUtils testUtils;
    private final ObjectMapper objectMapper;
    private List<User> users = new ArrayList<>();

    @Autowired
    public UserControllerTest(
            MockMvc mock,
            UserRepository repositoryOfUsers,
            PasswordEncoder encoder,
            JWTUtils utilsForJWT,
            TestUtils utilsForTests,
            ObjectMapper mapperOfObjects) {
        mockMvc = mock;
        userRepository = repositoryOfUsers;
        passwordEncoder = encoder;
        jwtUtils = utilsForJWT;
        testUtils = utilsForTests;
        objectMapper = mapperOfObjects;
    }

    @BeforeEach
    void setUp() {
        testUtils.clearRepositories();

        users = testUtils.createUsers(TEST_USER_COUNT);
    }

    @Test
    // Returns 200 and List<User>
    void indexTest() throws Exception {
        var actor = users.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(TEST_USER_COUNT));
    }

    @Test
    // Returns 200 and User
    void showTest() throws Exception {
        var actor = users.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                get("/api/users/{id}", actor.getId())
                                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(actor.getId());
        assertThat(context.read("$.firstName", String.class)).isEqualTo(actor.getFirstName());
        assertThat(context.read("$.lastName", String.class)).isEqualTo(actor.getLastName());
        assertThat(context.read("$.email", String.class)).isEqualTo(actor.getEmail());
        String createdAtStr = context.read("$.createdAt", String.class);
        LocalDateTime actualDate =
                LocalDateTime.parse(createdAtStr).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime expectedDate = actor.getCreatedAt().truncatedTo(ChronoUnit.SECONDS);
        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @Test
    // Returns status 201 and Body
    void createTest() throws Exception {
        var actor = users.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var dto = new UserCreateDTO();
        dto.setFirstName("Jack");
        dto.setLastName("Jones");
        dto.setEmail("jack@google.com");
        dto.setPassword("secret");

        var result =
                mockMvc.perform(
                                post("/api/users")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.firstName").value(dto.getFirstName()))
                        .andExpect(jsonPath("$.lastName").value(dto.getLastName()))
                        .andExpect(jsonPath("$.email").value(dto.getEmail()))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andReturn();

        var content = result.getResponse().getContentAsString();

        Long generatedId = JsonPath.parse(content).read("$.id", Long.class);

        var createdUser = userRepository.findById(generatedId);
        assertThat(createdUser).isPresent();

        boolean isPasswordMatched =
                passwordEncoder.matches(dto.getPassword(), createdUser.get().getPassword());
        assertThat(isPasswordMatched).isTrue();
    }

    @Test
    // Returns status 200 and the updated User
    void partialUpdateTest() throws Exception {
        var dto = new UserUpdateDTO();

        dto.setFirstName(JsonNullable.of("NewFirstName"));
        dto.setLastName(JsonNullable.of("NewLastName"));
        dto.setPassword(JsonNullable.of("NewPassword"));
        dto.setEmail(JsonNullable.undefined());

        var actor = users.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                put("/api/users/{id}", actor.getId())
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(actor.getId());
        assertThat(context.read("$.firstName", String.class)).isEqualTo(dto.getFirstName().get());
        assertThat(context.read("$.lastName", String.class)).isEqualTo(dto.getLastName().get());
        assertThat(context.read("$.email", String.class)).isEqualTo(actor.getEmail());

        String createdAtStr = context.read("$.createdAt", String.class);
        LocalDateTime actualDate =
                LocalDateTime.parse(createdAtStr).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime expectedDate = actor.getCreatedAt().truncatedTo(ChronoUnit.SECONDS);
        assertThat(actualDate).isEqualTo(expectedDate);

        var updatedUser = userRepository.findById(actor.getId());
        boolean isPasswordMatched =
                passwordEncoder.matches(dto.getPassword().get(), updatedUser.get().getPassword());
        assertThat(isPasswordMatched).isTrue();
    }

    @Test

    // Returns status 204
    void deleteTest() throws Exception {
        var actor = users.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        mockMvc.perform(
                        delete("/api/users/{id}", actor.getId())
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(actor.getId())).isNotPresent();
    }
}
