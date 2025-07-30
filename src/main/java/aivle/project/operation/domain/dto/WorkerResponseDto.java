package aivle.project.operation.domain.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerResponseDto {
    private Long workerId;
    private String loginId;
    private String employeeNumber;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate createdAt;

    public static WorkerResponseDto fromEntity(aivle.project.operation.domain.Worker worker) {
        return WorkerResponseDto.builder()
                .workerId(worker.getWorkerId())
                .loginId(worker.getLoginId())
                .employeeNumber(worker.getEmployeeNumber())
                .name(worker.getName())
                .email(worker.getEmail())
                .phoneNumber(worker.getPhoneNumber())
                .address(worker.getAddress())
                .createdAt(worker.getCreatedAt())
                .build();
    }
}
