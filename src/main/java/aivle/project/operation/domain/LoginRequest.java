package aivle.project.operation.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String adminId;
    private String password;
}
