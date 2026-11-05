package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        taskRepository = repository;
        taskMapper = mapper;
    }

    public List<TaskDTO> findAll() {
        return taskRepository.findAll().stream().map(taskMapper::makeDTO).toList();
    }

    public TaskDTO find(Long id) {
        var task =
                taskRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Task with ID " + id + " not found"));

        return taskMapper.makeDTO(task);
    }

    @Transactional
    public TaskDTO create(TaskCreateDTO dto) {
        var task = taskRepository.save(taskMapper.makeEntity(dto));

        return taskMapper.makeDTO(task);
    }

    @Transactional
    public TaskDTO update(Long id, TaskUpdateDTO dto) {
        var task =
                taskRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Task with ID " + id + " not found"));

        taskMapper.updateEntity(task, dto);

        return taskMapper.makeDTO(taskRepository.save(task));
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
