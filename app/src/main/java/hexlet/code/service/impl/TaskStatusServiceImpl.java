package hexlet.code.service.impl;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public TaskStatusServiceImpl(TaskStatusRepository repository, TaskStatusMapper mapper) {
        taskStatusRepository = repository;
        taskStatusMapper = mapper;
    }

    @Override
    public List<TaskStatusDTO> findAll(int page, int size) {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::makeDTO)
                .skip((long) (page - 1) * size)
                .limit(size)
                .toList();
    }

    @Override
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
    @Override
    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        var taskStatus = taskStatusRepository.save(taskStatusMapper.makeEntity(dto));

        return taskStatusMapper.makeDTO(taskStatus);
    }

    @Transactional
    @Override
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

    @Override
    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
