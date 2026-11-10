package hexlet.code.service;

import hexlet.code.dto.TaskParametersDTO;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import org.springframework.data.domain.Page;

public interface TaskService {
    Page<TaskDTO> findAllBy(TaskParametersDTO params, int page, int size);

    TaskDTO find(Long id);

    TaskDTO create(TaskCreateDTO dto);

    TaskDTO update(Long id, TaskUpdateDTO dto);

    void delete(Long id);
}
