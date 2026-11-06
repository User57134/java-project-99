package hexlet.code.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TaskParametersDTO {
    private String titleCont;

    private Long assigneeId;

    private String status;

    private Long labelId;
}
