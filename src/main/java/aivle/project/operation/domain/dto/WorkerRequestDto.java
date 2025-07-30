package aivle.project.operation.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerRequestDto {
    private String loginId;
    private String password;
    private String employeeNumber;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
}
