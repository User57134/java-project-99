package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TaskCreateDTO {

    @NotBlank(message = "Название задачи должно содержать как минимум 1 символ") private String title;

    private String content;

    private Integer index;

    @NotBlank(message = "Должен быть указан статус задачи") private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonProperty("taskLabelIds")
    private Set<Long> labelIds;
}
