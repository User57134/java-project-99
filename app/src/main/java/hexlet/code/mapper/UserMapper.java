package hexlet.code.mapper;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired private PasswordEncoder passwordEncoder;

    public abstract UserDTO makeDTO(User user);

    @Mapping(target = "password", source = "password", qualifiedByName = "encryptPassword")
    public abstract User makeEntity(UserCreateDTO dto);

    @Mapping(target = "password", source = "password", qualifiedByName = "encryptPassword")
    public abstract void updateEntity(@MappingTarget User user, UserUpdateDTO dto);

    /**
     * Собственный метод для реализации автоматического шифрования пароля. Вызывается при
     * преобразовании из CreateDTO, UpdateDTO в сущность. Внутри сущности пароль хранится в виде
     * хэша.
     */
    @Named("encryptPassword")
    protected String encryptPassword(String password) {
        if (password == null || password.isBlank()) {
            return null;
        }

        return passwordEncoder.encode(password);
    }
}
