package aivle.project.operation.domain.dto;

import aivle.project.operation.domain.Worker;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WorkerSignupRequestDto {
    private String loginId;
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
