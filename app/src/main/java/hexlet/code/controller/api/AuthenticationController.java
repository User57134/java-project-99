package hexlet.code.controller.api;

import hexlet.code.dto.AuthRequest;
import hexlet.code.util.JWTUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(
            JWTUtils utilsForJWT, AuthenticationManager managerOfAuthentication) {
        jwtUtils = utilsForJWT;
        authenticationManager = managerOfAuthentication;
    }

    @PostMapping("/login")
    public String create(@RequestBody AuthRequest authRequest) {
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(), authRequest.getPassword());

        var result = authenticationManager.authenticate(authenticationToken);

        return jwtUtils.generateToken(result.getName(), result.getAuthorities());
    }
}
