package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.dto.label.LabelCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TaskCreateDTO {

    @NotBlank @Size(min = 1, message = "Название задачи должно содержать как минимум 1 символ") private String title;

    private String content;

    private Integer index;

    @NotBlank(message = "Должен быть указан статус задачи") private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @Valid private Set<LabelCreateDTO> labels;
}
