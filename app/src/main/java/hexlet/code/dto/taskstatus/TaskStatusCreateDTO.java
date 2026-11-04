package hexlet.code.dto.taskstatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TaskStatusCreateDTO {
    @NotBlank @Size(min = 1, message = "Название статуса должно содержать как минимум 1 символ") private String name;

    @NotBlank @Size(min = 1, message = "Слаг статуса должен содержать как минимум 1 символ") private String slug;

    private LocalDateTime createdAt;
}
