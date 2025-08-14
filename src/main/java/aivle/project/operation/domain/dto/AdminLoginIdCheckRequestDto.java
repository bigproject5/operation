package aivle.project.operation.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminLoginIdCheckRequestDto {

    @NotBlank
    @Size(min = 8, max = 20, message = "Login ID must be between 8 and 20 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Login ID must contain only letters and numbers.")
    private String loginId;
}
