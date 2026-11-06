package hexlet.code.service;

import hexlet.code.dto.TaskParametersDTO;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskSpecification taskSpecification;
    private final TaskMapper taskMapper;

    public TaskService(
            TaskRepository repository, TaskSpecification specification, TaskMapper mapper) {
        taskRepository = repository;
        taskSpecification = specification;
        taskMapper = mapper;
    }

    public Page<TaskDTO> findAllBy(TaskParametersDTO params, int page, int size) {
        var spec = taskSpecification.build(params);
        var tasksPage = taskRepository.findAll(spec, PageRequest.of(page - 1, size));

        return tasksPage.map(taskMapper::makeDTO);
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
