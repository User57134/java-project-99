package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.Role;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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
    private final LabelRepository labelRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository repositoryOfUsers,
            PasswordEncoder encoder,
            TaskStatusRepository repositoryOfStatuses,
            LabelRepository repositoryOfLabels) {
        userRepository = repositoryOfUsers;
        passwordEncoder = encoder;
        taskStatusRepository = repositoryOfStatuses;
        labelRepository = repositoryOfLabels;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("🚀 Start of the generation of pre-set data...");
        createAdmin();
        createAvailableTaskStatuses();
        createAvailableLabels();
        log.info("✅ The data generation finished.");
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
        var statusNames = List.of("draft", "ToReview", "ToBeFixed", "ToPublish", "Published");

        var taskStatuses =
                statusNames.stream()
                        .map(
                                name -> {
                                    var words = name.split("(?<=[a-zа-яё])(?=[A-ZА-ЯЁ])");

                                    StringBuilder slug = new StringBuilder(words[0].toLowerCase());
                                    if (words.length > 1) {
                                        for (int i = 1; i < words.length; i += 1) {
                                            slug.append("_").append(words[i].toLowerCase());
                                        }
                                    }

                                    var status = new TaskStatus();
                                    status.setName(name);
                                    status.setSlug(slug.toString());

                                    return status;
                                })
                        .toList();

        log.info("taskStatuses = " + taskStatuses);

        for (var status : taskStatuses) {
            if (taskStatusRepository.findBySlug(status.getSlug()).isEmpty()) {
                taskStatusRepository.save(status);
            }
        }
    }

    private void createAvailableLabels() {
        var labelNames = List.of("feature", "bugs");

        var labels =
                labelNames.stream()
                        .map(
                                name -> {
                                    var label = new Label();
                                    label.setName(name);
                                    return label;
                                })
                        .toList();

        for (var label : labels) {
            if (labelRepository.findByName(label.getName()).isEmpty()) {
                labelRepository.save(label);
            }
        }
    }
}
