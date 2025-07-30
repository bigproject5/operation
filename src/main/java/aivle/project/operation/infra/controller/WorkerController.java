package aivle.project.operation.infra.controller;

import aivle.project.operation.domain.Worker;
import aivle.project.operation.service.WorkerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/operation/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    //작업자 등록
//    @PostMapping
//    public ResponseEntity<Worker> register(@RequestBody Worker worker) {
//        Worker savedWorker = workerService.registerWorker(worker);
//        return ResponseEntity.ok(savedWorker);
//    }
    //전체 작업자 조회
    @GetMapping
    public ResponseEntity<List<Worker>> getAllWorkers() {
        return ResponseEntity.ok(workerService.getAllWorks());
    }
    //단일 작업자 조회
    @GetMapping("/{id}")
    public ResponseEntity<Worker> getWorkerById(@PathVariable Long id) {
        try {
            Worker worker = workerService.getWorkerById(id);
            return ResponseEntity.ok(worker);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // 작업자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        try {
            workerService.deleteWorker(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
