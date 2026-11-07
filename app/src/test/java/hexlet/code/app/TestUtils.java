package hexlet.code.app;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestUtils {

    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TestUtils(
            UserRepository repositoryOfUsers,
            TaskStatusRepository repositoryOfStatuses,
            TaskRepository repositoryOfTasks,
            LabelRepository repositoryOfLabels,
            PasswordEncoder encoder) {

        userRepository = repositoryOfUsers;
        taskStatusRepository = repositoryOfStatuses;
        taskRepository = repositoryOfTasks;
        labelRepository = repositoryOfLabels;
        passwordEncoder = encoder;
    }

    public void clearRepositories() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    public List<User> createUsers(int userCount) {
        List<User> rawUsers = new ArrayList<>();

        for (int i = 0; i < userCount; i += 1) {
            var user = new User();
            user.setFirstName("User" + i);
            user.setLastName("Lastname" + i);
            user.setEmail("user" + i + "@mail.com");
            user.setPassword(passwordEncoder.encode("" + i + i + i));
            rawUsers.add(user);
        }

        return userRepository.saveAll(rawUsers);
    }

    public List<TaskStatus> createStatuses(int statusCount) {
        List<TaskStatus> rawStatuses = new ArrayList<>();

        for (int i = 0; i < statusCount; i += 1) {
            var status = new TaskStatus();
            status.setName("Status" + i);
            status.setSlug("status_" + i + "_slug");
            rawStatuses.add(status);
        }

        return taskStatusRepository.saveAll(rawStatuses);
    }

    @Transactional
    public List<Task> createTasks(int taskCount) {
        var statuses = taskStatusRepository.findAll();
        var users = userRepository.findAll();
        var labels = labelRepository.findAll();

        if (statuses.isEmpty()) {
            createStatuses(taskCount);
            statuses = taskStatusRepository.findAll();
        }

        if (users.isEmpty()) {
            createUsers(taskCount);
            users = userRepository.findAll();
        }

        if (labels.isEmpty()) {
            createLabels(taskCount);
            labels = labelRepository.findAll();
        }

        List<Task> rawTasks = new ArrayList<>();

        for (int i = 0; i < taskCount; i += 1) {
            var task = new Task();
            task.setName("Task" + i);
            task.setDescription("Description" + i);
            task.setIndex(i);
            task.setAssignee(users.get(i));
            task.setTaskStatus(statuses.get(i));
            task.addLabel(labels.get(i));
            rawTasks.add(task);
        }

        return taskRepository.saveAll(rawTasks);
    }

    public List<Label> createLabels(int labelCount) {
        List<Label> rawLabels = new ArrayList<>();

        for (int i = 0; i < labelCount; i += 1) {
            var label = new Label();
            label.setName("Label" + i);
            rawLabels.add(label);
        }

        return labelRepository.saveAll(rawLabels);
    }

    public Set<Long> getStoredLabelsIds() {
        return labelRepository.findAll().stream().map(Label::getId).collect(Collectors.toSet());
    }
}
