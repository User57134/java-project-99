package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public TaskStatusService(TaskStatusRepository repository, TaskStatusMapper mapper) {
        taskStatusRepository = repository;
        taskStatusMapper = mapper;
    }

    public List<TaskStatusDTO> findAll() {
        return taskStatusRepository.findAll().stream().map(taskStatusMapper::makeDTO).toList();
    }

    public TaskStatusDTO find(Long id) {
        var taskStatus =
                taskStatusRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Task status with ID " + id + " not found"));

        return taskStatusMapper.makeDTO(taskStatus);
    }

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        var taskStatus = taskStatusRepository.save(taskStatusMapper.makeEntity(dto));

        return taskStatusMapper.makeDTO(taskStatus);
    }

    @Transactional
    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto) {
        var Status =
                taskStatusRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Task status with ID " + id + " not found"));

        taskStatusMapper.updateEntity(Status, dto);

        return taskStatusMapper.makeDTO(taskStatusRepository.save(Status));
    }

    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
