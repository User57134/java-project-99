package hexlet.code.specification;

import hexlet.code.dto.TaskParametersDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParametersDTO params) {
        return withTitleContained(params.getTitleCont())
                .and(
                        withAssigneeId(params.getAssigneeId())
                                .and(
                                        withStatus(params.getStatus())
                                                .and(withLabelId(params.getLabelId()))));
    }

    private Specification<Task> withTitleContained(String name) {
        return (root, query, cb) ->
                (name == null || name.isBlank())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
                (assigneeId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) ->
                status == null
                        ? cb.conjunction()
                        : cb.equal(root.get("taskStatus").get("slug"), status);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return cb.conjunction();
            }

            query.distinct(true);

            return cb.equal(root.join("labels").get("id"), labelId);
        };
    }
}
