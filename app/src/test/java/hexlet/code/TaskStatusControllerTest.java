package hexlet.code;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import hexlet.code.app.TestUtils;
import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {
    private final int TEST_STATUS_COUNT = 3;

    private final MockMvc mockMvc;
    private final TaskStatusRepository taskStatusRepository;
    private final JWTUtils jwtUtils;
    private final TestUtils testUtils;
    private final ObjectMapper objectMapper;
    private List<TaskStatus> taskStatuses = new ArrayList<>();
    private User actor = null;

    @Autowired
    public TaskStatusControllerTest(
            MockMvc mock,
            UserRepository repositoryOfUsers,
            TaskStatusRepository repositoryOfStatuses,
            JWTUtils utilsForJWT,
            TestUtils utilsForTests,
            ObjectMapper mapperOfObjects) {
        mockMvc = mock;
        taskStatusRepository = repositoryOfStatuses;
        jwtUtils = utilsForJWT;
        testUtils = utilsForTests;
        objectMapper = mapperOfObjects;
    }

    @BeforeEach
    void setUp() {
        testUtils.clearRepositories();

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

        String createdAtStr = context.read("$.createdAt", String.class);
        LocalDateTime actualDate =
                LocalDateTime.parse(createdAtStr).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime expectedDate = testStatus.getCreatedAt().truncatedTo(ChronoUnit.SECONDS);
        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @Test
    // Returns status 201 and Body
    void createTest() throws Exception {
        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var dto = new TaskStatusCreateDTO();
        dto.setName("To Do Something");
        dto.setSlug("to_do_something");

        var result =
                mockMvc.perform(
                                post("/api/task_statuses")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(dto.getName()))
                        .andExpect(jsonPath("$.slug").value(dto.getSlug()))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andReturn();

        var content = result.getResponse().getContentAsString();

        Long generatedId = JsonPath.parse(content).read("$.id", Long.class);

        assertThat(taskStatusRepository.findById(generatedId)).isPresent();
        assertThat(taskStatusRepository.findBySlug(dto.getSlug()).get().getId())
                .isEqualTo(generatedId);
    }

    @Test
    // Returns status 200 and the updated User
    void partialUpdateTest() throws Exception {
        var testStatus = taskStatuses.getFirst();

        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("NewStatusName"));
        dto.setSlug(JsonNullable.of("new_status_slug"));

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                put("/api/task_statuses/{id}", testStatus.getId())
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(testStatus.getId());
        assertThat(context.read("$.name", String.class)).isEqualTo(dto.getName().get());
        assertThat(context.read("$.slug", String.class)).isEqualTo(dto.getSlug().get());

        String createdAtStr = context.read("$.createdAt", String.class);
        LocalDateTime actualDate =
                LocalDateTime.parse(createdAtStr).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime expectedDate = testStatus.getCreatedAt().truncatedTo(ChronoUnit.SECONDS);
        assertThat(actualDate).isEqualTo(expectedDate);
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
