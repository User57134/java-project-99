package hexlet.code.dto.taskstatus;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class TaskStatusDTO {
    private Long id;
    private String name;
    private String slug;
    private LocalDateTime createdAt;
}
