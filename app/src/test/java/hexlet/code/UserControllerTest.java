package hexlet.code;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.JWTUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private final int TEST_USER_COUNT = 3;

    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private List<User> users = new ArrayList<>();

    @Autowired
    public UserControllerTest(
            MockMvc mock,
            UserRepository repositoryOfUsers,
            PasswordEncoder encoder,
            JWTUtils utilsForJWT) {
        mockMvc = mock;
        userRepository = repositoryOfUsers;
        passwordEncoder = encoder;
        jwtUtils = utilsForJWT;
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        users = createUsers(TEST_USER_COUNT);
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
    }

    @Test
    // Returns status 201 and Body
    void createTest() throws Exception {
        var actor = users.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var body =
                """
                            {
                               "email": "jack@google.com",
                               "firstName": "Jack",
                               "lastName": "Jons",
                               "password": "some-password"
                             }
                        """;

        var result =
                mockMvc.perform(
                                post("/api/users")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(body))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.firstName").value("Jack"))
                        .andExpect(jsonPath("$.lastName").value("Jons"))
                        .andExpect(jsonPath("$.email").value("jack@google.com"))
                        .andReturn();

        var content = result.getResponse().getContentAsString();

        Long generatedId = JsonPath.parse(content).read("$.id", Long.class);

        assertThat(userRepository.findById(generatedId)).isPresent();
    }

    @Test
    // Returns status 200 and the updated User
    void partialUpdateTest() throws Exception {
        var body =
                """
                            {
                               "firstName": "Jack",
                               "email": "jack@yahoo.com",
                               "password": "new-password"
                             }
                        """;

        var actor = users.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                put("/api/users/{id}", actor.getId())
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(body))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(actor.getId());
        assertThat(context.read("$.firstName", String.class)).isEqualTo("Jack");
        assertThat(context.read("$.lastName", String.class)).isEqualTo(actor.getLastName());
        assertThat(context.read("$.email", String.class)).isEqualTo("jack@yahoo.com");

        var updatedUser = userRepository.findById(actor.getId());
        boolean isPasswordMatched =
                passwordEncoder.matches("new-password", updatedUser.get().getPassword());
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

    private List<User> createUsers(int userCount) {
        List<User> rawUsers = new ArrayList<>();

        for (int i = 0; i < userCount; i += 1) {
            var user = new User();
            user.setFirstName("TestName" + i);
            user.setLastName("TestSurname" + i);
            user.setEmail("test" + i + "@mail.com");
            user.setPassword(passwordEncoder.encode("" + i + i + i));
            rawUsers.add(user);
        }

        return userRepository.saveAll(rawUsers);
    }
}
