package engine.controller;

import engine.dto.user.RegisterRequestDto;
import engine.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegisterController {
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
         userService.register(registerRequestDto);
    }
}
