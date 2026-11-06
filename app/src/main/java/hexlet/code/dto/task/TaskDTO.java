package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TaskDTO {
    private Long id;

    private String title;

    private String content;

    private Integer index;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String status;

    Set<Long> labelIds;

    private LocalDateTime createdAt;
}
