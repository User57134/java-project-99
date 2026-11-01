package hexlet.code.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AuthRequest {
    private String username;
    private String password;
}
