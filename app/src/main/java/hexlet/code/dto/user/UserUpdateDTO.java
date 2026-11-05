package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDTO {

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    @Email(message = "Некорректный формат для электронной почты") private JsonNullable<String> email;

    @Size(min = 3, message = "Пароль должен содержать как минимум 3 символа") private JsonNullable<String> password;
}
