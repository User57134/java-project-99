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
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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
public class LabelControllerTest {

    private final int TEST_LABEL_COUNT = 3;

    private final MockMvc mockMvc;
    private final LabelRepository labelRepository;
    private final JWTUtils jwtUtils;
    private final TestUtils testUtils;
    private final ObjectMapper objectMapper;
    private List<Label> labels = new ArrayList<>();
    private User actor = null;

    @Autowired
    public LabelControllerTest(
            MockMvc mock,
            LabelRepository repositoryOfLabels,
            JWTUtils utilsForJWT,
            TestUtils utilsForTests,
            ObjectMapper mapperOfObjects) {

        mockMvc = mock;
        labelRepository = repositoryOfLabels;
        jwtUtils = utilsForJWT;
        testUtils = utilsForTests;
        objectMapper = mapperOfObjects;
    }

    @BeforeEach
    void setUp() {
        testUtils.clearRepositories();

        labels = testUtils.createLabels(TEST_LABEL_COUNT);

        actor = testUtils.createUsers(1).getFirst();
    }

    @Test
    // Returns 200 and List<User>
    void indexTest() throws Exception {
        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        mockMvc.perform(get("/api/labels").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(TEST_LABEL_COUNT));
    }

    @Test
    // Returns 200 and User
    void showTest() throws Exception {
        var testLabel = labels.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                get("/api/labels/{id}", testLabel.getId())
                                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(testLabel.getId());
        assertThat(context.read("$.name", String.class)).isEqualTo(testLabel.getName());
        String createdAtStr = context.read("$.createdAt", String.class);
        LocalDateTime actualDate =
                LocalDateTime.parse(createdAtStr).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime expectedDate = testLabel.getCreatedAt().truncatedTo(ChronoUnit.SECONDS);
        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @Test
    // Returns status 201 and Body
    void createTest() throws Exception {
        var dto = new LabelCreateDTO();

        dto.setName("TestLabel");

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var result =
                mockMvc.perform(
                                post("/api/labels")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(dto.getName()))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andReturn();

        var content = result.getResponse().getContentAsString();

        Long generatedId = JsonPath.parse(content).read("$.id", Long.class);

        assertThat(labelRepository.findById(generatedId)).isPresent();
    }

    @Test
    // Returns status 200 and the updated User
    void partialUpdateTest() throws Exception {
        var testLabel = labels.getFirst();

        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("NewLabel"));

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        var content =
                mockMvc.perform(
                                put("/api/labels/{id}", testLabel.getId())
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        var context = JsonPath.parse(content);

        assertThat(context.read("$.id", Long.class)).isEqualTo(testLabel.getId());
        assertThat(context.read("$.name", String.class)).isEqualTo(dto.getName().get());

        String createdAtStr = context.read("$.createdAt", String.class);
        LocalDateTime actualDate =
                LocalDateTime.parse(createdAtStr).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime expectedDate = testLabel.getCreatedAt().truncatedTo(ChronoUnit.SECONDS);
        assertThat(actualDate).isEqualTo(expectedDate);

        assertThat(labelRepository.findByName(dto.getName().get()).get().getId())
                .isEqualTo(testLabel.getId());
    }

    @Test
    // Returns status 204
    void deleteTest() throws Exception {
        var testLabel = labels.getFirst();

        String token = jwtUtils.generateToken(actor.getEmail(), actor.getAuthorities());

        mockMvc.perform(
                        delete("/api/labels/{id}", testLabel.getId())
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.findById(testLabel.getId())).isNotPresent();
    }
}
