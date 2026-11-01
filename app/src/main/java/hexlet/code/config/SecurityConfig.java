package hexlet.code.config;

import hexlet.code.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtDecoder jwtDecoder;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtDecoder decoderOfJwt,
            PasswordEncoder encoderOfPassword,
            CustomUserDetailsService serviceOfUserDetails) {

        jwtDecoder = decoderOfJwt;
        passwordEncoder = encoderOfPassword;
        userDetailsService = serviceOfUserDetails;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Отключаем CSRF, так как API работает без сессий (Stateless)
                .csrf(csrf -> csrf.disable())

                // 2. Настраиваем правила доступа
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/", "/api/login", "/welcome")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/users")
                                        .permitAll() // создание пользователя доступно всем
                                        // Все остальные запросы требуют просто аутентификации
                                        .anyRequest()
                                        .authenticated())

                // 3. Отключаем хранение сессий на сервере
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Проверка JWT-токена в заголовке Authorization: Bearer <токен>
                .oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.decoder(jwtDecoder)))

                // Заголовка .httpBasic() здесь быть НЕ ДОЛЖНО
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public AuthenticationProvider daoAuthProvider(AuthenticationManagerBuilder auth) {
        var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
