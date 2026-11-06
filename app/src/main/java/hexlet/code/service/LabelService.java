package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public LabelService(LabelRepository repository, LabelMapper mapper) {
        labelRepository = repository;
        labelMapper = mapper;
    }

    public List<LabelDTO> findAll() {
        return labelRepository.findAll().stream().map(labelMapper::makeDTO).toList();
    }

    public LabelDTO find(Long id) {
        var label =
                labelRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Label with ID " + id + " not found"));

        return labelMapper.makeDTO(label);
    }

    @Transactional
    public LabelDTO create(LabelCreateDTO dto) {
        var label = labelRepository.save(labelMapper.makeEntity(dto));

        return labelMapper.makeDTO(label);
    }

    @Transactional
    public LabelDTO update(Long id, LabelUpdateDTO dto) {
        var label =
                labelRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Label with ID " + id + " not found"));

        labelMapper.updateEntity(label, dto);

        return labelMapper.makeDTO(labelRepository.save(label));
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
