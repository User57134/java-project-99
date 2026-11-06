package hexlet.code.dto.label;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LabelDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
