package hexlet.code.mapper;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {

    @Autowired LabelRepository labelRepository;

    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "labelIds", source = "labels")
    public abstract TaskDTO makeDTO(Task task);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "labels", source = "labels", qualifiedByName = "mapToLabelSet")
    public abstract Task makeEntity(TaskCreateDTO dto);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "labels", source = "labels", qualifiedByName = "mapToLabelSet")
    public abstract void updateEntity(@MappingTarget Task task, TaskUpdateDTO dto);

    /**
     * Собственный метод для отображения из Set<LabelCreateDTO> в Set<Label>. Он проверяет каждый
     * тег по имени: если тег уже есть в БД, берется он (вместе со своим ID). Если тега нет —
     * создается новый объект, который сохранится каскадом.
     */
    @Named("mapToLabelSet")
    protected Set<Label> mapStringToTags(Set<LabelCreateDTO> labelDTOs) {
        if (labelDTOs == null) {
            return new HashSet<>();
        }

        return labelDTOs.stream()
                .map(
                        dto ->
                                labelRepository
                                        .findByName(dto.getName())
                                        .orElseGet(
                                                () -> {
                                                    Label label = new Label();
                                                    label.setName(dto.getName());
                                                    return label;
                                                }))
                .collect(Collectors.toSet());
    }

    /**
     * Вспомогательный метод для того, чтобы MapStruct смог самостоятельно преобразовать Set<Label>
     * в Set<Long>. Взять в источнике поле id и вернуть целевой объект.
     */
    protected Long labelToId(Label label) {
        if (label == null) {
            return null;
        }
        return label.getId(); // Явно возвращаем id без вызова конструкторов Long
    }
}
