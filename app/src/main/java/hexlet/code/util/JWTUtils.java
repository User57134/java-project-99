package hexlet.code.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JWTUtils {

    private final JwtEncoder jwtEncoder;

    public JWTUtils(JwtEncoder encoder) {
        jwtEncoder = encoder;
    }

    public String generateToken(
            String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();

        // 2. Превращаем права в строку, разделенную пробелами (стандарт для Spring Security OAuth2)
        String scope =
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(" "));

        log.info("scope = " + scope);

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(now)
                        .expiresAt(now.plus(1, ChronoUnit.HOURS))
                        .subject(username)
                        .claim("scope", scope) // 3. Записываем права в claim "scope"
                        .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
