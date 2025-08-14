package aivle.project.operation.service;

import aivle.project.operation.domain.AttachedFile;
import aivle.project.operation.domain.AttachedFileRepository;
import aivle.project.operation.domain.Notice;
import aivle.project.operation.infra.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileUploadService {

    private final AttachedFileRepository attachedFileRepository;
    private final FileUploadConfig fileUploadConfig;

    /**
     * 파일 업로드 처리
     */
    public AttachedFile uploadFile(MultipartFile file, Notice notice) {
        try {
            // 1. 파일 유효성 검사
            validateFile(file);

            // 2. 안전한 파일명 생성
            String storedFileName = generateSecureFileName(file.getOriginalFilename());

            // 3. 파일 확장자 추출
            String fileExtension = getFileExtension(file.getOriginalFilename());

            // 4. 저장 경로 생성
            String datePath = createDatePath();
            String fullPath = fileUploadConfig.getUploadPath() + datePath;
            createDirectoryIfNotExists(fullPath);

            // 5. 파일 저장
            String filePath = fullPath + "/" + storedFileName;
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);

            // 6. 파일 실행 권한 제거
            removeExecutePermission(destinationFile);

            // 7. 파일 정보 DB 저장
            AttachedFile attachedFile = AttachedFile.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .fileExtension(fileExtension)
                    .mimeType(file.getContentType())
                    .fileSize(file.getSize())
                    .filePath(datePath + "/" + storedFileName)
                    .notice(notice)
                    .build();

            return attachedFileRepository.save(attachedFile);

        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일 유효성 검사
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일을 선택해주세요.");
        }

        if (file.getSize() > FileUploadConfig.MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 10MB를 초과했습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 파일명입니다.");
        }

        String extension = getFileExtension(originalFilename);
        if (!FileUploadConfig.ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다. 허용 형식: " +
                    String.join(", ", FileUploadConfig.ALLOWED_EXTENSIONS));
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !FileUploadConfig.ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("허용되지 않은 파일 타입입니다.");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String generateSecureFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String createDatePath() {
        LocalDate now = LocalDate.now();
        return now.getYear() + "/" +
                String.format("%02d", now.getMonthValue()) + "/" +
                String.format("%02d", now.getDayOfMonth());
    }

    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void removeExecutePermission(File file) {
        try {
            file.setExecutable(false, false);
            file.setExecutable(false, true);
        } catch (Exception e) {
            log.warn("실행 권한 제거 실패: {}", e.getMessage());
        }
    }

    /**
     * 파일 목록 조회
     */
    @Transactional(readOnly = true)
    public List<AttachedFile> getFilesByNoticeId(Long noticeId) {
        return attachedFileRepository.findByNoticeId(noticeId);
    }

    /**
     * 파일 다운로드
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadFile(Long fileId) {
        try {
            AttachedFile fileInfo = attachedFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

            String fullPath = fileUploadConfig.getUploadPath() + fileInfo.getFilePath();
            Path filePath = Paths.get(fullPath);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("파일이 존재하지 않습니다.");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileInfo.getMimeType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" +
                                    URLEncoder.encode(fileInfo.getOriginalFileName(), "UTF-8") + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 개별 파일 삭제
     */
    @Transactional
    public void deleteFile(Long fileId) {
        log.info("파일 삭제 시작 - fileId: {}", fileId);
        
        try {
            // 1. DB에서 파일 정보 조회
            AttachedFile fileInfo = attachedFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다. ID: " + fileId));

            // 2. 물리적 파일 삭제
            String fullPath = fileUploadConfig.getUploadPath() + fileInfo.getFilePath();
            File physicalFile = new File(fullPath);
            
            if (physicalFile.exists()) {
                boolean deleted = physicalFile.delete();
                if (!deleted) {
                    log.warn("물리적 파일 삭제 실패: {}", fullPath);
                } else {
                    log.info("물리적 파일 삭제 완료: {}", fullPath);
                }
            } else {
                log.warn("물리적 파일이 존재하지 않음: {}", fullPath);
            }

            // 3. DB에서 파일 정보 삭제
            attachedFileRepository.delete(fileInfo);
            
            log.info("파일 삭제 완료 - fileId: {}, originalFileName: {}", fileId, fileInfo.getOriginalFileName());
            
        } catch (Exception e) {
            log.error("파일 삭제 실패 - fileId: {}, error: {}", fileId, e.getMessage());
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 공지사항에 속한 여러 파일들 삭제
     */
    @Transactional
    public void deleteFiles(List<Long> fileIds) {
        log.info("여러 파일 삭제 시작 - fileIds: {}", fileIds);
        
        if (fileIds == null || fileIds.isEmpty()) {
            log.info("삭제할 파일이 없습니다.");
            return;
        }
        
        for (Long fileId : fileIds) {
            try {
                deleteFile(fileId);
            } catch (Exception e) {
                log.error("파일 삭제 실패 - fileId: {}, 계속 진행합니다.", fileId);
                // 개별 파일 삭제 실패 시에도 다른 파일들은 계속 삭제 진행
            }
        }
        
        log.info("여러 파일 삭제 완료 - 요청된 파일 수: {}", fileIds.size());
    }

    /**
     * 공지사항에 속한 모든 파일 삭제
     */
    @Transactional
    public void deleteAllFilesByNotice(Notice notice) {
        log.info("공지사항의 모든 파일 삭제 - noticeId: {}", notice.getId());
        
        List<AttachedFile> files = attachedFileRepository.findByNotice(notice);
        
        for (AttachedFile file : files) {
            try {
                // 물리적 파일 삭제
                String fullPath = fileUploadConfig.getUploadPath() + file.getFilePath();
                File physicalFile = new File(fullPath);
                
                if (physicalFile.exists()) {
                    physicalFile.delete();
                }
                
                // Notice와의 연관관계 해제
                notice.removeFile(file);
                
            } catch (Exception e) {
                log.error("파일 삭제 실패 - fileId: {}", file.getId());
            }
        }
        
        // DB에서 파일 정보들 삭제
        attachedFileRepository.deleteAll(files);
        
        log.info("공지사항의 모든 파일 삭제 완료 - noticeId: {}, 삭제된 파일 수: {}", notice.getId(), files.size());
    }
}