package aivle.project.operation.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workerId;

    private String loginId;
    private String password;
    private String employeeNumber;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate createdAt;
}
