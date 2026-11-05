package hexlet.code.component;

import hexlet.code.model.Role;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository repositoryOfUsers,
            PasswordEncoder encoder,
            TaskStatusRepository repositoryOfStatuses) {
        userRepository = repositoryOfUsers;
        passwordEncoder = encoder;
        taskStatusRepository = repositoryOfStatuses;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("🚀 Старт автоматической генерации данных для DEV...");
        createAdmin();
        createAvailableTaskStatuses();
        log.info("✅ База данных успешно наполнена!");
    }

    private void createAdmin() {
        if (userRepository.findByEmail("hexlet@example.com").isPresent()) {
            return;
        }

        User user = new User();
        user.setEmail("hexlet@example.com");
        user.setPassword(passwordEncoder.encode("qwerty"));
        user.setFirstName("Admin");
        user.setLastName("Hexlet");
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    private void createAvailableTaskStatuses() {
        var statusNames = List.of("draft", "To Review", "To Be Fixed", "To Publish", "Published");

        var taskStatuses =
                statusNames.stream()
                        .map(
                                name -> {
                                    var words = name.toLowerCase().split(" ");

                                    StringBuilder slug = new StringBuilder(words[0]);
                                    if (words.length > 1) {
                                        for (int i = 1; i < words.length; i += 1) {
                                            slug.append("_").append(words[i]);
                                        }
                                    }

                                    var status = new TaskStatus();
                                    status.setName(name);
                                    status.setSlug(slug.toString());

                                    return status;
                                })
                        .toList();

        for (var s : taskStatuses) {
            if (taskStatusRepository.findBySlug(s.getSlug()).isEmpty()) {
                taskStatusRepository.save(s);
            }
        }
    }
}
