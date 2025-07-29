package aivle.project.operation.service;

// 필수 Import 문들 (모두 추가 필요)
import aivle.project.operation.domain.dto.NoticeDetailResponseDto;
import aivle.project.operation.domain.dto.NoticeListResponseDto;
import aivle.project.operation.domain.dto.NoticeCreateRequestDto;
import aivle.project.operation.domain.dto.NoticeUpdateRequestDto;
import aivle.project.operation.domain.Notice;
import aivle.project.operation.domain.NoticeRepository;
import aivle.project.operation.infra.exception.NoticeNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 목록 조회 (페이징)
     */
    public Page<NoticeListResponseDto> getNoticeList(int page, int size) {
        log.info("공지사항 목록 조회 - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> notices = noticeRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);

        return notices.map(NoticeListResponseDto::from);
    }

    /**
     * 공지사항 상세 조회 (조회수 증가)
     */
    @Transactional
    public NoticeDetailResponseDto getNoticeDetail(Long id) {
        log.info("공지사항 상세 조회 - id: {}", id);

        Notice notice = noticeRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));

        // 조회수 증가
        notice.increaseViewCount();

        return NoticeDetailResponseDto.from(notice);
    }

    /**
     * 공지사항 생성
     */
    @Transactional
    public NoticeDetailResponseDto createNotice(NoticeCreateRequestDto requestDto) {
        log.info("공지사항 생성 - title: {}, admin: {}", requestDto.getTitle(), requestDto.getAdmin());

        Notice notice = requestDto.toEntity();
        Notice savedNotice = noticeRepository.save(notice);

        log.info("공지사항 생성 완료 - id: {}", savedNotice.getId());
        return NoticeDetailResponseDto.from(savedNotice);
    }
    /**
     * 공지사항 수정
     */
    @Transactional
    public NoticeDetailResponseDto updateNotice(Long noticeId, NoticeUpdateRequestDto updateDto) {
        log.info("공지사항 수정 시작 = id: {}", noticeId);
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다. ID: " + noticeId));

        notice.update(updateDto.getTitle(), updateDto.getContent());

        log.info("공지사항 수정 완료 - id: {}", notice.getId());


        return NoticeDetailResponseDto.from(notice);
    }

    /**
     * 제목으로 공지사항 검색
     */
    public Page<NoticeListResponseDto> searchNoticesByTitle(String title, int page, int size) {
        log.info("공지사항 제목 검색 - title: {}, page: {}, size: {}", title, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> notices = noticeRepository
                .findByIsActiveTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(title, pageable);

        return notices.map(NoticeListResponseDto::from);
    }

    /**
     * 작성자로 공지사항 검색
     */
    public Page<NoticeListResponseDto> searchNoticesByAdmin(String admin, int page, int size) {
        log.info("공지사항 작성자 검색 - admin: {}, page: {}, size: {}", admin, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> notices = noticeRepository
                .findByIsActiveTrueAndAdminContainingIgnoreCaseOrderByCreatedAtDesc(admin, pageable);

        return notices.map(NoticeListResponseDto::from);
    }

//    /**
//     * 인기 공지사항 조회 (조회수 기준)
//     */
//    public Page<NoticeListResponseDto> getPopularNotices(int page, int size) {
//        log.info("인기 공지사항 조회 - page: {}, size: {}", page, size);
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Notice> notices = noticeRepository.findPopularNotices(pageable);
//
//        return notices.map(NoticeListResponseDto::from);
//    }

    /**
     * 공지사항 삭제 (논리적 삭제)
     */
    @Transactional
    public void deleteNotice(Long id) {
        log.info("공지사항 삭제 - id: {}", id);

        Notice notice = noticeRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));

        notice.deactivate();
        log.info("공지사항 삭제 완료 - id: {}", id);
    }
}
