package aivle.project.operation.domain.dto;

import aivle.project.operation.domain.Admin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private String companyNumber;
    private String address;
    private String adminCode;

    public Admin toEntity(String encodedPassword) {
        Admin admin = new Admin();
        admin.setLoginId(this.loginId);
        admin.setPassword(encodedPassword);
        admin.setName(this.name);
        admin.setEmail(this.email);
        admin.setPhoneNumber(this.phoneNumber);
        admin.setCompanyNumber(this.companyNumber);
        admin.setAddress(this.address);
        admin.setAdminCode(this.adminCode);
        return admin;
    }
}
