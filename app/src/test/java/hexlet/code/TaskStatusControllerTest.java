package hexlet.code;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import hexlet.code.app.TestUtils;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
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
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {
    private final int TEST_STATUS_COUNT = 3;

    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final JWTUtils jwtUtils;
    private final TestUtils testUtils;
    private List<TaskStatus> taskStatuses = new ArrayList<>();
    private User actor = null;

    @Autowired
    public TaskStatusControllerTest(
            MockMvc mock,
            UserRepository repositoryOfUsers,
            TaskStatusRepository repositoryOfStatuses,
            JWTUtils utilsForJWT,
            TestUtils utilsForTests) {
        mockMvc = mock;
        userRepository = repositoryOfUsers;
        taskStatusRepository = repositoryOfStatuses;
        jwtUtils = utilsForJWT;
        testUtils = utilsForTests;
    }

    @BeforeEach
    void setUp() {
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        taskStatuses = testUtils.createStatuses(TEST_STATUS_COUNT);

        actor = testUtils.createUsers(1).getFirst();
    }

    @Test
    // Returns 200 and List<User>
    void indexTest() throws Exception {
        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        mockMvc.perform(get("/api/task_statuses").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(TEST_STATUS_COUNT));
    }

    @Test
    // Returns 200 and User
    void showTest() throws Exception {
        var testStatus = taskStatuses.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                get("/api/task_statuses/{id}", testStatus.getId())
                                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(testStatus.getId());
        assertThat(context.read("$.name", String.class)).isEqualTo(testStatus.getName());
        assertThat(context.read("$.slug", String.class)).isEqualTo(testStatus.getSlug());
    }

    @Test
    // Returns status 201 and Body
    void createTest() throws Exception {
        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var body =
                """
                            {
                               "name": "To Do Something",
                               "slug": "to_do_something"
                             }
                        """;

        var result =
                mockMvc.perform(
                                post("/api/task_statuses")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(body))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value("To Do Something"))
                        .andExpect(jsonPath("$.slug").value("to_do_something"))
                        .andReturn();

        var content = result.getResponse().getContentAsString();

        Long generatedId = JsonPath.parse(content).read("$.id", Long.class);

        assertThat(taskStatusRepository.findById(generatedId)).isPresent();
    }

    @Test
    // Returns status 200 and the updated User
    void partialUpdateTest() throws Exception {
        var testStatus = taskStatuses.getFirst();

        var body =
                """
                            {
                               "name": "To Do Something NEW",
                               "slug": "to_do_something_new"
                             }
                        """;

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                put("/api/task_statuses/{id}", testStatus.getId())
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(body))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(testStatus.getId());
        assertThat(context.read("$.name", String.class)).isEqualTo("To Do Something NEW");
        assertThat(context.read("$.slug", String.class)).isEqualTo("to_do_something_new");
    }

    @Test

    // Returns status 204
    void deleteTest() throws Exception {
        var testStatus = taskStatuses.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        mockMvc.perform(
                        delete("/api/task_statuses/{id}", testStatus.getId())
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.findById(testStatus.getId())).isNotPresent();
    }
}
