package com.anton.webquizengine.controller;

import jakarta.validation.Valid;
import com.anton.webquizengine.dto.user.RegisterRequestDto;
import com.anton.webquizengine.service.UserService;
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
