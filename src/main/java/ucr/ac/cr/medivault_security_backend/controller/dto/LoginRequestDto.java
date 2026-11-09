package ucr.ac.cr.medivault_security_backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank(message = "Username is required.")
        @Size(min = 4, max = 50, message = "Username: 4 to 50 characters.")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Alphanumeric characters only.")
        String username,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 100, message = "Password: minimum 8 characters.")
        String password
) {}