package aivle.project.operation.infra;

import aivle.project.operation.domain.dto.NoticeUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 프로젝트 클래스들 import
import aivle.project.operation.domain.dto.NoticeListResponseDto;
import aivle.project.operation.domain.dto.NoticeCreateRequestDto;
import aivle.project.operation.service.NoticeService;
import aivle.project.operation.domain.dto.NoticeDetailResponseDto;


@RestController
@RequestMapping("/api/operation/notices")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // 개발 환경용, 운영 시 특정 도메인으로 제한
public class NoticeController {

    private final NoticeService noticeService; // public → private 수정

    /**
     * 공지사항 목록 조회 API
     * GET /api/notices?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<NoticeListResponseDto>> getNoticeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/notices - page: {}, size: {}", page, size);

        Page<NoticeListResponseDto> notices = noticeService.getNoticeList(page, size);
        return ResponseEntity.ok(notices);
    }

    /**
     * 공지사항 상세 조회 API
     * GET /api/notices/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoticeDetailResponseDto> getNoticeDetail(@PathVariable Long id) {
        log.info("GET /api/notices/{}", id);

        NoticeDetailResponseDto notice = noticeService.getNoticeDetail(id);
        return ResponseEntity.ok(notice);
    }

    /**
     * 공지사항 생성 API
     * POST /api/notices
     */
    @PostMapping
    public ResponseEntity<NoticeDetailResponseDto> createNotice(
            @RequestHeader("Admin") String adminId,
            @RequestHeader("Name") String name,
            @Valid @RequestBody NoticeCreateRequestDto requestDto) {

        log.info("POST /api/notices - title: {}", requestDto.getTitle());

        NoticeDetailResponseDto createdNotice = noticeService.createNotice(requestDto, Long.valueOf(adminId), name);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotice);
    }

    /**
     * 공지사항 수정 API
     * GET /api/notices/{id}
     */
    @PutMapping("/{noticeId}")
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
     */
    @GetMapping("/search/title")
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
     */
    @GetMapping("/search/adminId")
    public ResponseEntity<Page<NoticeListResponseDto>> searchNoticesByAdminId(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/notices/search/adminId - keyword: {}, page: {}, size: {}", keyword, page, size);

        Page<NoticeListResponseDto> notices = noticeService.searchNoticesByAdmin(keyword, page, size);
        return ResponseEntity.ok(notices);
    }

//    /**
//     * 인기 공지사항 조회 API
//     * GET /api/notices/popular?page=0&size=10
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
     * DELETE /api/notices/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        log.info("DELETE /api/notices/{}", id);

        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health Check API
     * GET /api/notices/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notice Service is running!");
    }
}