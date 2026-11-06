package hexlet.code.repository;

import hexlet.code.model.Label;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    /* Поиск метки по имени */
    Optional<Label> findByName(String name);
}
