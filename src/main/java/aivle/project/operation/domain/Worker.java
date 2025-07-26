package aivle.project.operation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Table
@Getter
@Setter
@Entity
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long workerId;
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private String companyNumber;
    private String address;
    private LocalDate createdAt;
}
