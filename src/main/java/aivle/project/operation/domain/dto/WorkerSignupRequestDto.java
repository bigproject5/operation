package aivle.project.operation.domain.dto;

import aivle.project.operation.domain.Worker;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WorkerSignupRequestDto {
    @NotBlank(message = "Login ID must not be empty.")
    @Size(min = 8, max = 20, message = "Login ID must be between 8 and 20 characters.")
    private String loginId;
    @NotBlank(message = "Password must not be empty.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,32}$",
            message = "Password must be 8–32 characters long and include at least one number and one special character."
    )
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private String companyNumber;
    private String address;

    public Worker toEntity(String password) {
        Worker worker = new Worker();
        LocalDate date = LocalDate.now();
        worker.setLoginId(loginId);
        worker.setPassword(password);
        worker.setName(name);
        worker.setEmail(email);
        worker.setPhoneNumber(phoneNumber);
        worker.setCompanyNumber(companyNumber);
        worker.setAddress(address);
        worker.setCreatedAt(date);
        return worker;
    }
}
