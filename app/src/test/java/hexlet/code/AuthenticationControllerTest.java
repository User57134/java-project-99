package hexlet.code;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.dto.AuthRequest;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    private final String TEST_USERNAME = "test@test.com";
    private final String TEST_PASSWORD = "test";

    private final MockMvc mockMvc;
    private final TestUtils testUtils;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthenticationControllerTest(
            MockMvc mock,
            UserRepository repositoryOfUsers,
            TestUtils utilsForTests,
            ObjectMapper mapperOfObjects) {

        mockMvc = mock;
        testUtils = utilsForTests;
        objectMapper = mapperOfObjects;
    }

    @BeforeEach
    public void setUp() {
        testUtils.clearRepositories();
        testUtils.createTestUser(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    public void testCorrectLogin() throws Exception {
        var authRequest = new AuthRequest();
        authRequest.setUsername(TEST_USERNAME);
        authRequest.setPassword(TEST_PASSWORD);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testIncorrectLogin() throws Exception {
        var authRequest = new AuthRequest();
        authRequest.setUsername("not a mail");
        authRequest.setPassword("test");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testIncorrectPassword() throws Exception {
        var authRequest = new AuthRequest();
        authRequest.setUsername("test@test.com");
        authRequest.setPassword("   ");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAbsentLogin() throws Exception {
        var authRequest = new AuthRequest();
        authRequest.setUsername("absent@mail.com");
        authRequest.setPassword("test");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }
}
