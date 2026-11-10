package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> findAll(int page, int size);

    UserDTO find(Long id);

    UserDTO create(UserCreateDTO dto);

    UserDTO update(Long id, UserUpdateDTO dto);

    void delete(Long id);
}
