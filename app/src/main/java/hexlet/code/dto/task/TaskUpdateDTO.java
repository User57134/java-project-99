package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@NoArgsConstructor
@Getter
@Setter
public class TaskUpdateDTO {
    @NotBlank @Size(min = 1, message = "Название задачи должно содержать как минимум 1 символ") private JsonNullable<String> title;

    private JsonNullable<String> content;

    private JsonNullable<Integer> index;

    @NotBlank(message = "Должен быть указан статус задачи") private JsonNullable<String> status;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;
}
