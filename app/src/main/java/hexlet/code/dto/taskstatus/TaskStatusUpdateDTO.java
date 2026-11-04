package hexlet.code.dto.taskstatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@NoArgsConstructor
@Getter
@Setter
public class TaskStatusUpdateDTO {
    @NotBlank @Size(min = 1, message = "Название статуса должно содержать как минимум 1 символ") private JsonNullable<String> name;

    @NotBlank @Size(min = 1, message = "Слаг статуса должен содержать как минимум 1 символ") private JsonNullable<String> slug;
}
