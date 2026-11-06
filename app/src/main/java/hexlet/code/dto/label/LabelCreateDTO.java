package hexlet.code.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LabelCreateDTO {
    @NotBlank @Size(min = 3, max = 1000, message = "Длинна названия метки должна быть от 3 до 1000 символов") private String name;
}
