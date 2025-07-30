package aivle.project.operation.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String token;
    private String role;
    private Long expiresIn;

}
