package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import java.util.List;

public interface LabelService {
    List<LabelDTO> findAll(int page, int size);

    LabelDTO find(Long id);

    LabelDTO create(LabelCreateDTO dto);

    LabelDTO update(Long id, LabelUpdateDTO dto);

    void delete(Long id);
}
