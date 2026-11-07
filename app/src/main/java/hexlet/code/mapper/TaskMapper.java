package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import java.util.Set;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;
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
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "mapLongsToLabels")
    public abstract Task makeEntity(TaskCreateDTO dto);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(
            target = "labels",
            source = "labelIds",
            qualifiedByName = "mapJsonNullableLongsToLabels")
    public abstract void updateEntity(@MappingTarget Task task, TaskUpdateDTO dto);

    /**
     * Вспомогательный метод для того, чтобы MapStruct смог самостоятельно преобразовать Set<Label>
     * в Set<Long>. Взять в источнике поле id и вернуть целевой объект.
     */
    protected Long mapToLong(Label label) {
        return label.getId();
    }

    /**
     * Вспомогательный метод для преобразования Set<Long> в Set<Label>. Для ускорения - выгружает
     * сразу все элементы.
     */
    @Named("mapLongsToLabels")
    protected Set<Label> mapLongsToLabels(Set<Long> labelIds) {
        if (labelIds == null) {
            return null;
        }

        return new java.util.HashSet<>(labelRepository.findAllById(labelIds));
    }

    /**
     * Вспомогательный метод для преобразования JsonNullable<Set<Long>> в Set<Label>. Для ускорения
     * - выгружает сразу все элементы.
     */
    @Named("mapJsonNullableLongsToLabels")
    protected Set<Label> mapJsonNullableLongsToLabels(JsonNullable<Set<Long>> jsonNullableIds) {
        if (jsonNullableIds == null || !jsonNullableIds.isPresent()) {
            return null;
        }

        Set<Long> ids = jsonNullableIds.get();
        if (ids == null) {
            return null;
        }

        return new java.util.HashSet<>(labelRepository.findAllById(ids));
    }
}
