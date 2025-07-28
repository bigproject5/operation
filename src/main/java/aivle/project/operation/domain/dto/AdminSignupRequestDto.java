package aivle.project.operation.domain.dto;

import aivle.project.operation.domain.Admin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AdminSignupRequestDto {

    @NotBlank(message = "Login ID must not be empty.")
    @Size(min = 8, max = 20, message = "Login ID must be between 8 and 20 characters.")
    private String loginId;

    @NotBlank(message = "Password must not be empty.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,32}$",
            message = "Password must be 8–32 characters long and include at least one number and one special character."
    )
    private String password;

    @NotBlank(message = "name must not be empty.")
    private String name;

    private String email;

    private String phoneNumber;

    @NotBlank(message = "Employee number must not be empty.")
    private String employeeNumber;

    private String address;

    @NotBlank(message = "admin code must not be empty.")
    private String adminCode;

    public Admin toEntity(String encodedPassword) {
        Admin admin = new Admin();
        admin.setLoginId(this.loginId);
        admin.setPassword(encodedPassword);
        admin.setName(this.name);
        admin.setEmail(this.email);
        admin.setPhoneNumber(this.phoneNumber);
        admin.setEmployeeNumber(this.employeeNumber);
        admin.setAddress(this.address);
        admin.setAdminCode(this.adminCode);
        return admin;
    }
}
