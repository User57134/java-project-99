package hexlet.code.app;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public TestUtils(
            UserRepository repositoryOfUsers,
            TaskStatusRepository repositoryOfStatuses,
            PasswordEncoder encoder) {
        userRepository = repositoryOfUsers;
        taskStatusRepository = repositoryOfStatuses;
        passwordEncoder = encoder;
    }

    public List<User> createUsers(int userCount) {
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

    public List<TaskStatus> createStatuses(int statusCount) {
        List<TaskStatus> rawStatuses = new ArrayList<>();

        for (int i = 0; i < statusCount; i += 1) {
            var status = new TaskStatus();
            status.setName("Name" + i);
            status.setSlug("Slug" + i);
            rawStatuses.add(status);
        }

        return taskStatusRepository.saveAll(rawStatuses);
    }
}
