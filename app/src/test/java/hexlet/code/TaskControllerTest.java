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
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.util.JWTUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    private final int TEST_TASK_COUNT = 3;

    private final MockMvc mockMvc;
    private final TaskRepository taskRepository;
    private final JWTUtils jwtUtils;
    private final TestUtils testUtils;
    private final ObjectMapper objectMapper;
    private List<Task> tasks = new ArrayList<>();
    private User actor = null;

    @Autowired
    public TaskControllerTest(
            MockMvc mock,
            TaskRepository repositoryOfTasks,
            JWTUtils utilsForJWT,
            TestUtils utilsForTests,
            ObjectMapper mapperOfObjects) {

        mockMvc = mock;
        taskRepository = repositoryOfTasks;
        jwtUtils = utilsForJWT;
        testUtils = utilsForTests;
        objectMapper = mapperOfObjects;
    }

    @BeforeEach
    void setUp() {
        testUtils.clearRepositories();

        tasks = testUtils.createTasks(TEST_TASK_COUNT);

        actor = tasks.getFirst().getAssignee();
    }

    @Test
    // Returns 200 and List<User>
    void indexTest() throws Exception {
        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        mockMvc.perform(get("/api/tasks").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(TEST_TASK_COUNT));
    }

    @Test
    // Returns 200 and User
    void showTest() throws Exception {
        var testTask = tasks.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                get("/api/tasks/{id}", testTask.getId())
                                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(testTask.getId());
        assertThat(context.read("$.title", String.class)).isEqualTo(testTask.getName());
        assertThat(context.read("$.content", String.class)).isEqualTo(testTask.getDescription());
        assertThat(context.read("$.index", Integer.class)).isEqualTo(testTask.getIndex());
        assertThat(context.read("$.assignee_id", Long.class))
                .isEqualTo(testTask.getAssignee().getId());
        assertThat(context.read("$.status", String.class))
                .isEqualTo(testTask.getTaskStatus().getSlug());

        String createdAtStr = context.read("$.createdAt", String.class);
        LocalDateTime actualDate =
                LocalDateTime.parse(createdAtStr).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime expectedDate = testTask.getCreatedAt().truncatedTo(ChronoUnit.SECONDS);
        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @Test
    // Returns status 201 and Body
    void createTest() throws Exception {
        var dto = new TaskCreateDTO();

        dto.setTitle("Test title");
        dto.setContent("Test content");
        dto.setIndex(12);
        dto.setAssigneeId(actor.getId());
        dto.setStatus(tasks.getFirst().getTaskStatus().getSlug());

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var result =
                mockMvc.perform(
                                post("/api/tasks")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.title").value(dto.getTitle()))
                        .andExpect(jsonPath("$.content").value(dto.getContent()))
                        .andExpect(jsonPath("$.index").value(dto.getIndex()))
                        .andExpect(jsonPath("$.status").value(dto.getStatus()))
                        .andExpect(jsonPath("$.assignee_id").value(dto.getAssigneeId()))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andReturn();

        var content = result.getResponse().getContentAsString();

        Long generatedId = JsonPath.parse(content).read("$.id", Long.class);

        assertThat(taskRepository.findById(generatedId)).isPresent();
    }

    @Test
    // Returns status 200 and the updated User
    void partialUpdateTest() throws Exception {
        var testTask = tasks.getFirst();

        var dto = new TaskUpdateDTO();
        dto.setTitle(JsonNullable.of("New Title"));
        dto.setContent(JsonNullable.of("New content"));
        dto.setIndex(JsonNullable.undefined());
        dto.setAssigneeId(JsonNullable.undefined());
        dto.setStatus(JsonNullable.undefined());

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                put("/api/tasks/{id}", testTask.getId())
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(testTask.getId());
        assertThat(context.read("$.title", String.class)).isEqualTo(dto.getTitle().get());
        assertThat(context.read("$.content", String.class)).isEqualTo(dto.getContent().get());
        assertThat(context.read("$.index", Integer.class)).isEqualTo(testTask.getIndex());
        assertThat(context.read("$.assignee_id", Long.class))
                .isEqualTo(testTask.getAssignee().getId());
        assertThat(context.read("$.status", String.class))
                .isEqualTo(testTask.getTaskStatus().getSlug());

        String createdAtStr = context.read("$.createdAt", String.class);
        LocalDateTime actualDate =
                LocalDateTime.parse(createdAtStr).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime expectedDate = testTask.getCreatedAt().truncatedTo(ChronoUnit.SECONDS);
        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @Test
    // Returns status 204
    void deleteTest() throws Exception {
        var testTask = tasks.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        mockMvc.perform(
                        delete("/api/tasks/{id}", testTask.getId())
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(testTask.getId())).isNotPresent();
    }
}
