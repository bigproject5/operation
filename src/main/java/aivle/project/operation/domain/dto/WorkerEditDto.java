package aivle.project.operation.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerEditDto {
    private Long WorkerId;
    private String loginId;
    private String employeeNumber;
    private String name;
    private String taskType;
    private String email;
    private String phoneNumber;
    private String address;
    private String profileImageUrl;
}
