package aivle.project.operation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table
@Entity
@Getter
@Setter
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long adminId;
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private String companyNumber;
    private String address;
    private String adminCode;

}
