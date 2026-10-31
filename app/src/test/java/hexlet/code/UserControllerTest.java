package hexlet.code;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
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
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    private final int TEST_USER_COUNT = 3;

    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private List<User> users = new ArrayList<>();

    @Autowired
    public UserControllerTest(
            MockMvc mock, UserRepository repositoryOfUsers, PasswordEncoder encoder) {
        mockMvc = mock;
        userRepository = repositoryOfUsers;
        passwordEncoder = encoder;
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        users = createUsers(TEST_USER_COUNT);
    }

    @Test
    // Returns 200 and List<User>
    void indexTest() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(TEST_USER_COUNT));
    }

    @Test
    // Returns 200 and User
    void showTest() throws Exception {
        var user = users.getFirst();

        var content =
                mockMvc.perform(get("/api/users/{id}", user.getId()))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(user.getId());
        assertThat(context.read("$.firstName", String.class)).isEqualTo(user.getFirstName());
        assertThat(context.read("$.lastName", String.class)).isEqualTo(user.getLastName());
        assertThat(context.read("$.email", String.class)).isEqualTo(user.getEmail());
    }

    @Test
    // Returns status 201 and Body
    void createTest() throws Exception {
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

        var user = users.getFirst();

        var content =
                mockMvc.perform(
                                put("/api/users/{id}", user.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(body))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(user.getId());
        assertThat(context.read("$.firstName", String.class)).isEqualTo("Jack");
        assertThat(context.read("$.lastName", String.class)).isEqualTo(user.getLastName());
        assertThat(context.read("$.email", String.class)).isEqualTo("jack@yahoo.com");

        var updatedUser = userRepository.findById(user.getId());
        assertThat(updatedUser.get().getPassword().equals(passwordEncoder.encode("new-password")));
    }

    @Test
    // Returns status 204
    void deleteTest() throws Exception {
        var user = users.getFirst();

        mockMvc.perform(delete("/api/users/{id}", user.getId())).andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isNotPresent();
    }

    private List<User> createUsers(int userCount) {
        List<User> rawUsers = new ArrayList<>();

        for (int i = 0; i < userCount; i += 1) {
            var user = new User();
            user.setFirstName("TestName" + i);
            user.setLastName("TestSurname" + i);
            user.setEmail("test" + i + "@mail.com");
            user.setPassword("" + i + i + i);
            rawUsers.add(user);
        }

        return userRepository.saveAll(rawUsers);
    }
}
