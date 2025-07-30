package aivle.project.operation.worker.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workerId;

    private String loginId;
    private String password;
    private String companyNumber;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
}
