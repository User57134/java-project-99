package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        userRepository = repository;
        userMapper = mapper;
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::makeDTO).toList();
    }

    public UserDTO find(Long id) {
        var user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User with ID " + id + " not found"));

        return userMapper.makeDTO(user);
    }

    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        var post = userRepository.save(userMapper.makeEntity(dto));

        return userMapper.makeDTO(post);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        var user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User with ID " + id + " not found"));

        userMapper.updateEntity(user, dto);

        return userMapper.makeDTO(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
