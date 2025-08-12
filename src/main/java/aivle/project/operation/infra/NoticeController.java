package aivle.project.operation.infra;

import aivle.project.operation.domain.AttachedFile;
import aivle.project.operation.domain.dto.NoticeUpdateRequestDto;
import aivle.project.operation.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

import aivle.project.operation.domain.dto.NoticeListResponseDto;
import aivle.project.operation.domain.dto.NoticeCreateRequestDto;
import aivle.project.operation.service.NoticeService;
import aivle.project.operation.domain.dto.NoticeDetailResponseDto;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/operation/notices")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NoticeController {

    private final NoticeService noticeService;
    private final FileUploadService fileUploadService;

    // 허용된 파일 확장자 목록
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "txt", "hwp", "jpg", "jpeg", "png", "gif"
    );

    /**
     * 공지사항 목록 조회 API
     * GET /api/notices?page=0&size=10     */    @GetMapping
    public ResponseEntity<Page<NoticeListResponseDto>> getNoticeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/notices - page: {}, size: {}", page, size);

        Page<NoticeListResponseDto> notices = noticeService.getNoticeList(page, size);
        return ResponseEntity.ok(notices);
    }

    /**
     * 공지사항 상세 조회 API
     * GET /api/operation/notices/notices/{id}     */    @GetMapping("/{id}")
    public ResponseEntity<NoticeDetailResponseDto> getNoticeDetail(@PathVariable Long id) {
        log.info("GET /api/operation/notices/{}", id);

        NoticeDetailResponseDto notice = noticeService.getNoticeDetail(id);
        return ResponseEntity.ok(notice);
    }

    /**
     * 공지사항 생성 API
     * POST /api/notices     */    @PostMapping
    public ResponseEntity<NoticeDetailResponseDto> createNotice(
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Name") String name,
            @Valid @RequestBody NoticeCreateRequestDto requestDto) {

        log.info("POST /api/notices - title: {}", requestDto.getTitle());
        NoticeDetailResponseDto createdNotice = noticeService.createNotice(requestDto, Long.valueOf(adminId), name);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotice);
    }

    // === 새로 추가: 파일과 함께 공지사항 생성 ===
    @PostMapping("/with-files")
    public ResponseEntity<NoticeDetailResponseDto> createNoticeWithFiles(
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Name") String name,
            @RequestPart("notice") @Valid NoticeCreateRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        log.info("POST /api/notices/with-files - title: {}, files count: {}",
                requestDto.getTitle(), files != null ? files.size() : 0);

        // 공지사항 생성
        NoticeDetailResponseDto createdNotice = noticeService.createNotice(requestDto, Long.valueOf(adminId), name);

        // 파일 업로드 처리
        if (files != null && !files.isEmpty()) {
            noticeService.uploadFilesToNotice(createdNotice.getId(), files);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotice);
    }


    /**
     * 공지사항 수정 API
     * GET /api/notices/{id}     */    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeDetailResponseDto> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeUpdateRequestDto updateDto
    ) {
        NoticeDetailResponseDto responseDto = noticeService.updateNotice(noticeId, updateDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 공지사항 제목 검색 API
     * GET /api/notices/search/title?keyword=검색어&page=0&size=10
     */    @GetMapping("/search/title")
    public ResponseEntity<Page<NoticeListResponseDto>> searchNoticesByTitle(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/notices/search/title - keyword: {}, page: {}, size: {}", keyword, page, size);

        Page<NoticeListResponseDto> notices = noticeService.searchNoticesByTitle(keyword, page, size);
        return ResponseEntity.ok(notices);
    }

    /**
     * 공지사항 작성자 검색 API
     * GET /api/notices/search/adminId?keyword=작성자&page=0&size=10
     */    @GetMapping("/search/adminId")
    public ResponseEntity<Page<NoticeListResponseDto>> searchNoticesByAdminId(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/notices/search/adminId - keyword: {}, page: {}, size: {}", keyword, page, size);

        Page<NoticeListResponseDto> notices = noticeService.searchNoticesByAdmin(keyword, page, size);
        return ResponseEntity.ok(notices);
    }

//    /**
//     * 인기 공지사항 조회 API//     * GET /api/notices/popular?page=0&size=10
//     */
//    @GetMapping("/popular")
//    public ResponseEntity<Page<NoticeListResponseDto>> getPopularNotices(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        log.info("GET /api/notices/popular - page: {}, size: {}", page, size);
//
//        Page<NoticeListResponseDto> notices = noticeService.getPopularNotices(page, size);
//        return ResponseEntity.ok(notices);
//    }

    /**
     * 공지사항 삭제 API (논리적 삭제)
     * DELETE /api/notices/{id}     */    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        log.info("DELETE /api/notices/{}", id);

        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health Check API
     * GET /api/notices/health     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notice Service is running!");
    }

    // === 새로 추가: 파일 관련 API들 ===

    /**
     * 특정 공지사항의 첨부파일 목록 조회
     */
    @GetMapping("/{noticeId}/files")
    public ResponseEntity<List<AttachedFile>> getNoticeFiles(@PathVariable Long noticeId) {
        log.info("GET /api/operation/notices/{}/files", noticeId);
        List<AttachedFile> files = fileUploadService.getFilesByNoticeId(noticeId);
        return ResponseEntity.ok(files);
    }

    /**
     * 파일 다운로드
     */
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        log.info("GET /api/operation/notices/files/{}/download", fileId);
        return fileUploadService.downloadFile(fileId);
    }

    /**
     * 기존 공지사항에 파일 추가 업로드
     */
    @PostMapping("/{noticeId}/files")
    public ResponseEntity<List<AttachedFile>> uploadFilesToNotice(
            @PathVariable Long noticeId,
            @RequestParam("files") List<MultipartFile> files) {

        log.info("POST /api/operation/notices/{}/files - files count: {}", noticeId, files.size());
        List<AttachedFile> uploadedFiles = noticeService.uploadFilesToNotice(noticeId, files);
        return ResponseEntity.ok(uploadedFiles);
    }

}