package hexlet.code.util;

import hexlet.code.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userUtils")
public class UserUtils {

    private final UserRepository userRepository;

    public UserUtils(UserRepository repositoryOfUsers) {
        userRepository = repositoryOfUsers;
    }

    public boolean isOwner(Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return userRepository
                .findById(id)
                .map(user -> user.getUsername().equals(authentication.getName()))
                .orElse(false);
    }
}
