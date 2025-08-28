package aivle.project.operation.domain.dto;

import aivle.project.operation.domain.Worker;
import aivle.project.operation.service.LoginService;
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
    private String taskType;
    private String email;
    private String phoneNumber;
    private String address;
    private String profileImageUrl;
    private LocalDate createdAt;

    public static WorkerResponseDto fromEntity(Worker worker) {
        return WorkerResponseDto.builder()
                .workerId(worker.getWorkerId())
                .loginId(worker.getLoginId())
                .employeeNumber(worker.getEmployeeNumber())
                .name(LoginService.maskName(worker.getName()))
                .taskType(worker.getTaskType())
                .email(worker.getEmail())
                .phoneNumber(worker.getPhoneNumber())
                .address(worker.getAddress())
                .profileImageUrl(worker.getProfileImageUrl())
                .createdAt(worker.getCreatedAt())
                .build();
    }
}
