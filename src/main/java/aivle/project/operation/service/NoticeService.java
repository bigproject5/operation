package aivle.project.operation.service;

// 필수 Import 문들 (모두 추가 필요)
import aivle.project.operation.domain.UploadFile;
import aivle.project.operation.domain.UploadFileRepository;
import aivle.project.operation.domain.dto.NoticeDetailResponseDto;
import aivle.project.operation.domain.dto.NoticeListResponseDto;
import aivle.project.operation.domain.dto.NoticeCreateRequestDto;
import aivle.project.operation.domain.dto.NoticeUpdateRequestDto;
import aivle.project.operation.domain.Notice;
import aivle.project.operation.domain.NoticeRepository;
import aivle.project.operation.infra.exception.FileUploadException;
import aivle.project.operation.infra.exception.NoticeNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UploadFileRepository uploadFileRepository;
    private final FileService fileService;

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

        Notice notice = noticeRepository.findByIdWithFiles(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));

        // 조회수 증가
        notice.increaseViewCount();

        return NoticeDetailResponseDto.from(notice);
    }

    /**
     * 공지사항 생성
     */
    @Transactional
    public NoticeDetailResponseDto createNotice(
            NoticeCreateRequestDto requestDto,
            Long adminId,
            String encodedName,
            List<UploadFile> fileList
    ) {
        log.info("공지사항 생성 - title: {}", requestDto.getTitle());

        String name = URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
        Notice notice = requestDto.toEntity(adminId, name);

        fileList.forEach(notice::addFile);

        Notice savedNotice = noticeRepository.save(notice);

        log.info("공지사항 생성 완료 - id: {}", savedNotice.getId());

        return NoticeDetailResponseDto.from(savedNotice);
    }
    /**
     * 공지사항 수정
     */
    @Transactional
    public NoticeDetailResponseDto updateNotice(Long noticeId, List<MultipartFile> files, NoticeUpdateRequestDto updateDto) {
        log.info("공지사항 수정 시작 - id: {}", noticeId);
        Notice notice = findNoticeById(noticeId);
        notice.update(updateDto.getTitle(), updateDto.getContent());
        deleteRequestedFiles(notice, updateDto.getRemoveFileIds());
        uploadNewFiles(notice, files);
        log.info("공지사항 수정 완료 - id: {}", notice.getId());
        return NoticeDetailResponseDto.from(notice);
    }

    private Notice findNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다. ID: " + noticeId));
    }

    private void deleteRequestedFiles(Notice notice, List<Long> removeFileIds) {
        if (removeFileIds == null || removeFileIds.isEmpty()) {
            log.debug("삭제할 파일이 없습니다.");
            return;
        }

        log.info("삭제할 파일 IDs: {}", removeFileIds);

        for (Long fileId : removeFileIds) {
            deleteFileById(notice, fileId);
        }
    }

    private void deleteFileById(Notice notice, Long fileId) {
        log.info("파일 삭제 처리 시작 - ID: {}", fileId);

        Optional<UploadFile> uploadFile = uploadFileRepository.findById(fileId);
        if (uploadFile.isEmpty()) {
            log.warn("삭제할 파일을 찾을 수 없습니다 - ID: {}", fileId);
            return;
        }

        deletePhysicalFile(uploadFile.get());
        notice.removeFile(fileId);
        log.info("파일 삭제 완료 - ID: {}", fileId);
    }

    private void deletePhysicalFile(UploadFile uploadFile) {
        String fileUrl = uploadFile.getFileUrl();
        Path filePath = Paths.get(System.getProperty("user.dir"), fileUrl);

        try {
            if (Files.deleteIfExists(filePath)) {
                log.info("물리 파일 삭제 성공: {}", filePath);
            } else {
                log.warn("물리 파일이 존재하지 않습니다: {}", filePath);
            }
        } catch (IOException e) {
            log.error("물리 파일 삭제 실패: {}", fileUrl, e);
        }
    }

    private void uploadNewFiles(Notice notice, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            log.debug("업로드할 파일이 없습니다.");
            return;
        }

        log.info("새 파일 업로드 시작 - 파일 수: {}", files.size());

        try {
            List<UploadFile> uploadedFiles = fileService.uploadFile(files);
            uploadedFiles.forEach(notice::addFile);
            log.info("새 파일 업로드 완료 - 업로드된 파일 수: {}", uploadedFiles.size());
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다.", e);
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
