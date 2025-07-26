package aivle.project.operation.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponse {
    private String loginId;
    private String name;
    private String email;
    private String phoneNumber;
    private String companyNumber;
    private String address;
}
