package hexlet.code.repository;

import hexlet.code.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /* Поиск записи по email */
    Optional<User> findByEmail(String email);
}
