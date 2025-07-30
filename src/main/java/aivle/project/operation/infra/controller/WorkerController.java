package aivle.project.operation.infra.controller;

import aivle.project.operation.domain.Worker;
import aivle.project.operation.service.WorkerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import aivle.project.operation.domain.dto.WorkerRequestDto;
import aivle.project.operation.domain.dto.WorkerResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/operation/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    //작업자 등록
//    @PostMapping
//    public ResponseEntity<WorkerResponseDto> register(@RequestBody WorkerRequestDto requestDto) {
//        Worker worker = Worker.builder()
//                .loginId(requestDto.getLoginId())
//                .password(requestDto.getPassword())
//                .employeeNumber(requestDto.getEmployeeNumber())
//                .name(requestDto.getName())
//                .email(requestDto.getEmail())
//                .phoneNumber(requestDto.getPhoneNumber())
//                .address(requestDto.getAddress())
//                .createdAt(LocalDate.now())
//                .build();
//
//        Worker savedWorker = workerService.registerWorker(worker);
//        return ResponseEntity.ok(WorkerResponseDto.fromEntity(savedWorker));
//    }
    //전체 작업자 조회
    @GetMapping
    public ResponseEntity<List<WorkerResponseDto>> getAllWorkers() {
        List<Worker> workers = workerService.getAllWorks();
        List<WorkerResponseDto> dtos = workers.stream()
                .map(WorkerResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    //단일 작업자 조회
    @GetMapping("/{id}")
    public ResponseEntity<WorkerResponseDto> getWorkerById(@PathVariable Long id) {
        Worker worker = workerService.getWorkerById(id);
        return ResponseEntity.ok(WorkerResponseDto.fromEntity(worker));
    }

    // 작업자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }
}
