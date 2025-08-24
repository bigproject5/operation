package aivle.project.operation.service;

import aivle.project.operation.domain.Worker;
import aivle.project.operation.domain.WorkerRepository;
import aivle.project.operation.domain.dto.WorkerEditDto;
import aivle.project.operation.domain.dto.WorkerRequestDto;
import aivle.project.operation.domain.dto.WorkerResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkerService {
    private final WorkerRepository workerRepository;

    public List<Worker> getAllWorks() {
        return workerRepository.findAll();
    }

    public Worker getWorkerById(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 작업자를 찾을 수 없습니다: "+id));
        String maskedName = LoginService.maskName(worker.getName());
        worker.setName(maskedName);
        return worker;
    }

    public void deleteWorker(Long id) {
        if (!workerRepository.existsById(id)) {
            throw new EntityNotFoundException("삭제하려는 작업자가 존재하지 않습니다: "+id);
        }
        workerRepository.deleteById(id);
    }

    public List<WorkerResponseDto> getAllWorkersByTask(String taskType) {
        List<Worker> workers = workerRepository.findAllByTaskType(taskType);
        return workers.stream()
                .map(WorkerResponseDto::fromEntity)
                .toList();
    }

    public WorkerResponseDto editWorker(WorkerEditDto workerEditDto) {
        log.info(workerEditDto.toString());

        Optional<Worker> worker_ = workerRepository.findByWorkerId(workerEditDto.getWorkerId());
        if(worker_.isEmpty()) {
            throw new IllegalArgumentException("작업자를 찾을 수 없습니다");
        }
        Worker worker = worker_.get();
        worker.setAddress(workerEditDto.getAddress());
        worker.setEmail(workerEditDto.getEmail());
        worker.setTaskType(workerEditDto.getTaskType());
        worker.setPhoneNumber(workerEditDto.getPhoneNumber());
        worker.setName(workerEditDto.getName());
//        worker.setProfileImageUrl(workerEditDto.getProfileImageUrl());
        workerRepository.save(worker);

        return WorkerResponseDto.fromEntity(worker);
    }
}
