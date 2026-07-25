package engine.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 5)
    private String password;

    public RegisterRequestDto() {}

    public RegisterRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
