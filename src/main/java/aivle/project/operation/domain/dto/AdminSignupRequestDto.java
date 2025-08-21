package aivle.project.operation.domain.dto;

import aivle.project.operation.domain.Admin;
import jakarta.validation.constraints.Email;
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
    @Pattern(
            regexp = "^[A-Za-z0-9]{8,20}$",
            message = "Login ID must contain only letters and numbers."
    )
    private String loginId;

    @NotBlank(message = "Password must not be empty.")
    @Size(min = 8, message = "Password must be over 8 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,}$",
            message = "Password must include at least one lowercase letter, one uppercase letter, one number, and one special character."
    )
    private String password;

    @NotBlank(message = "Name must not be empty.")
    @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters.")
    @Pattern(
            regexp = "^[가-힣a-zA-Z\\s]{2,20}$",
            message = "Name must contain only Korean characters, English letters, or spaces."
    )
    private String name;

    @NotBlank(message = "Email must not be empty.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotBlank(message = "Phone number must not be empty.")
    @Pattern(
            regexp = "^\\d+$",
            message = "Phone number must contain only numbers."
    )
    private String phoneNumber;

    @NotBlank(message = "Employee number must not be empty.")
    private String employeeNumber;

    @NotBlank(message = "Address must not be empty.")
    private String address;

    @NotBlank(message = "Admin code must not be empty.")
    private String adminCode;

    private String reCaptchaToken;

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