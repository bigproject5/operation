package aivle.project.operation.infra.controller;

import aivle.project.operation.domain.Worker;
import aivle.project.operation.domain.dto.RequestDto;
import aivle.project.operation.domain.dto.WorkerEditDto;
import aivle.project.operation.service.WorkerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Fetch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import aivle.project.operation.domain.dto.WorkerRequestDto;
import aivle.project.operation.domain.dto.WorkerResponseDto;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/operation/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

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

    @PutMapping("/edit")
    public ResponseEntity<WorkerResponseDto> editWorkerProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestBody WorkerEditDto workerEditDto
    ) {
        log.info("editWorkerProfile" + workerEditDto.toString());
        if (role.equals("WORKER"))
            workerEditDto.setWorkerId(Long.parseLong(userId));
        WorkerResponseDto responseDto = workerService.editWorker(workerEditDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/profile")
    public ResponseEntity<WorkerResponseDto> getWorkerByToken(
            @RequestHeader("X-User-Id") String userId
    ) {
        Long Id = Long.parseLong(userId);
        Worker worker = workerService.getWorkerById(Id);
        return ResponseEntity.ok(WorkerResponseDto.fromEntity(worker));
    }

    // 작업자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }

    //작업기준으로 작업자 조회
    @GetMapping("/task")
    public ResponseEntity<List<WorkerResponseDto>> getAllWorkersByTask(@RequestParam String taskType) {
        return ResponseEntity.ok(workerService.getAllWorkersByTask(taskType));
    }
}
