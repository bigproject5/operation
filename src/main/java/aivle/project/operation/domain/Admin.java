package aivle.project.operation.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Column(unique = true)
    @NotBlank(message = "Login ID must not be empty.")
    private String loginId;

    @NotBlank(message = "Password must not be empty.")
    private String password;

    private String name;
    private String email;
    private String phoneNumber;
    private String companyNumber;
    private String address;
    private String adminCode;

}
