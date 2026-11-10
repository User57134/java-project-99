package hexlet.code.service.impl;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        userRepository = repository;
        userMapper = mapper;
    }

    @Override
    public List<UserDTO> findAll(int page, int size) {
        return userRepository.findAll().stream()
                .map(userMapper::makeDTO)
                .skip((long) (page - 1) * size)
                .limit(size)
                .toList();
    }

    @Override
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
    @Override
    public UserDTO create(UserCreateDTO dto) {
        var user = userRepository.save(userMapper.makeEntity(dto));

        return userMapper.makeDTO(user);
    }

    @Transactional
    @Override
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

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
