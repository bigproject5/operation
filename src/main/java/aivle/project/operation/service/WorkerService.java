package aivle.project.operation.service;

import aivle.project.operation.domain.Worker;
import aivle.project.operation.domain.WorkerRepository;
import aivle.project.operation.domain.dto.WorkerResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;

//    public Worker registerWorker(Worker worker) {
//        return workerRepository.save(worker);
//    }
    public List<Worker> getAllWorks() {
        return workerRepository.findAll();
    }

    public Worker getWorkerById(Long id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 작업자를 찾을 수 없습니다: "+id));
    }

    public void deleteWorker(Long id) {
        if (!workerRepository.existsById(id)) {
            throw new EntityNotFoundException("삭제하려는 작업자가 존재하지 않습니다: "+id);
        }
        workerRepository.deleteById(id);
    }

    public List<WorkerResponseDto> getAllWorkersByTask(String task) {
        List<Worker> workers = workerRepository.findAllByTaskType(task);
        return workers.stream()
                .map(WorkerResponseDto::fromEntity)
                .toList();
    }
}
