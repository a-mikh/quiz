package com.anton.webquizengine.service;

import com.anton.webquizengine.dto.user.RegisterRequestDto;
import com.anton.webquizengine.exception.user.EmailAlreadyExistsException;
import com.anton.webquizengine.model.User;
import com.anton.webquizengine.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequestDto registerRequestDto) {
        boolean isEmailAlreadyExists = userRepository.existsByEmail(registerRequestDto.getEmail());

        if (isEmailAlreadyExists) {
            throw new EmailAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(registerRequestDto.getPassword());
        User user = new User(registerRequestDto.getEmail(), encodedPassword);

        userRepository.save(user);
    }
}
