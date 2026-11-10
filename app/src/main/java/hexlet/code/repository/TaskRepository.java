package hexlet.code.repository;

import hexlet.code.model.Task;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    @Override
    @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
    Page<Task> findAll(Specification<Task> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"labels"})
    Optional<Task> findTaskWithLabelsById(Long id);
}
