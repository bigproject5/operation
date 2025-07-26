package aivle.project.operation.domain.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponseDto {
    private String loginId;
    private String name;
    private String email;
    private String phoneNumber;
    private String companyNumber;
    private String address;

    public SignupResponseDto(SignupRequestDto signupResponse) {
        this.loginId = signupResponse.getLoginId();
        this.name = signupResponse.getName();
        this.email = signupResponse.getEmail();
        this.phoneNumber = signupResponse.getPhoneNumber();
        this.companyNumber = signupResponse.getCompanyNumber();
        this.address = signupResponse.getAddress();
    }
}
