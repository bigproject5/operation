package aivle.project.operation.service;

// 필수 Import 문들 (모두 추가 필요)
import aivle.project.operation.domain.AttachedFile;
import aivle.project.operation.domain.dto.NoticeDetailResponseDto;
import aivle.project.operation.domain.dto.NoticeListResponseDto;
import aivle.project.operation.domain.dto.NoticeCreateRequestDto;
import aivle.project.operation.domain.dto.NoticeUpdateRequestDto;
import aivle.project.operation.domain.Notice;
import aivle.project.operation.domain.NoticeRepository;
import aivle.project.operation.infra.NoticeNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FileUploadService fileUploadService;

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
    public NoticeDetailResponseDto createNotice(NoticeCreateRequestDto requestDto, Long adminId, String encodedName) {
        log.info("공지사항 생성 - title: {}", requestDto.getTitle());

        String name = URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
        Notice notice = requestDto.toEntity(adminId, name);
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
     * 공지사항 수정 (파일 삭제 포함)
     */
    @Transactional
    public NoticeDetailResponseDto updateNoticeWithFiles(Long noticeId, NoticeUpdateRequestDto updateDto) {
        log.info("공지사항 수정 시작 - id: {}", noticeId);
        
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다. ID: " + noticeId));

        boolean hasChanges = false;

        // 1. 필수 항목 검증 및 변경사항 확인 (제목, 내용)
        if (updateDto.getTitle() == null || updateDto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (updateDto.getContent() == null || updateDto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }

        // 2. 변경사항이 있는 경우에만 수정
        if (!notice.getTitle().equals(updateDto.getTitle()) || 
            !notice.getContent().equals(updateDto.getContent())) {
            notice.update(updateDto.getTitle(), updateDto.getContent());
            hasChanges = true;
            log.info("공지사항 내용 변경 완료 - id: {}", notice.getId());
        }

        // 3. 첨부파일 삭제 처리 (선택 작업)
        List<Long> deleteFileIds = updateDto.getDeleteFileIds();
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            try {
                fileUploadService.deleteFiles(deleteFileIds);
                hasChanges = true;
                log.info("첨부파일 삭제 완료 - noticeId: {}, 삭제된 파일 수: {}", noticeId, deleteFileIds.size());
            } catch (Exception e) {
                log.warn("첨부파일 삭제 실패 - noticeId: {}, error: {}", noticeId, e.getMessage());
            }
        }

        // 4. 변경사항이 있을 때만 저장
        if (hasChanges) {
            Notice savedNotice = noticeRepository.save(notice);
            log.info("공지사항 저장 완료 - id: {}", savedNotice.getId());
            return NoticeDetailResponseDto.from(savedNotice);
        } else {
            log.info("변경사항이 없어 저장하지 않음 - id: {}", noticeId);
            return NoticeDetailResponseDto.from(notice);
        }
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
    public Page<NoticeListResponseDto> searchNoticesByAdmin(String adminId, int page, int size) {
        log.info("공지사항 작성자 검색 - adminId: {}, page: {}, size: {}", adminId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> notices = noticeRepository
                .findByIsActiveTrueAndAdminIdContainingIgnoreCaseOrderByCreatedAtDesc(adminId, pageable);

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
     * 공지사항 삭제
     */
    @Transactional
    public void deleteNotice(Long id) {
        log.info("공지사항 삭제 - id: {}", id);

        Notice notice = noticeRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));

        notice.deactivate();
        log.info("공지사항 삭제 완료 - id: {}", id);
    }

    /**
     * 기존 공지사항에 파일들 업로드
     */
    @Transactional
    public List<AttachedFile> uploadFilesToNotice(Long noticeId, List<MultipartFile> files) {
        log.info("공지사항에 파일 업로드 - noticeId: {}, files count: {}", noticeId, files.size());

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeId));

        List<AttachedFile> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                AttachedFile uploadedFile = fileUploadService.uploadFile(file, notice);
                uploadedFiles.add(uploadedFile);
                notice.addFile(uploadedFile);
            }
        }

        log.info("파일 업로드 완료 - noticeId: {}, uploaded count: {}", noticeId, uploadedFiles.size());
        return uploadedFiles;
    }
}
