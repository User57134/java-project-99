package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentryTestController {

    @GetMapping("/sentrytest")
    public void test() throws Exception {
        throw new Exception("Тестовая автоматическая ошибка для проверки Bugsink!");
    }
}
