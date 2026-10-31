package hexlet.code.component;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository repositoryOfUsers, PasswordEncoder encoder) {
        userRepository = repositoryOfUsers;
        passwordEncoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        createSpecialUser();
    }

    private void createSpecialUser() {
        if (userRepository.findByEmail("hexlet@example.com").isPresent()) {
            return;
        }

        User user = new User();
        user.setEmail("hexlet@example.com");
        user.setPassword(passwordEncoder.encode("qwerty"));
        user.setFirstName("User");
        user.setLastName("Special");
        userRepository.save(user);
    }
}
