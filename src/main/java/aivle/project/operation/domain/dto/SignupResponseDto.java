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
    private String employeeNumber;
    private String address;

    public SignupResponseDto(AdminSignupRequestDto signupResponse) {
        this.loginId = signupResponse.getLoginId();
        this.name = signupResponse.getName();
        this.email = signupResponse.getEmail();
        this.phoneNumber = signupResponse.getPhoneNumber();
        this.employeeNumber = signupResponse.getEmployeeNumber();
        this.address = signupResponse.getAddress();
    }

    public SignupResponseDto(WorkerSignupRequestDto workerSignupRequestDto) {
        this.loginId = workerSignupRequestDto.getLoginId();
        this.name = workerSignupRequestDto.getName();
        this.email = workerSignupRequestDto.getEmail();
        this.phoneNumber = workerSignupRequestDto.getPhoneNumber();
        this.employeeNumber = workerSignupRequestDto.getEmployeeNumber();
        this.address = workerSignupRequestDto.getAddress();
    }
}
