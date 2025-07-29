package aivle.project.operation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 활성화된 공지사항만 페이징으로 조회 (최신순)
    Page<Notice> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    // 활성화된 공지사항 상세 조회
    Optional<Notice> findByIdAndIsActiveTrue(Long id);

    // 제목으로 검색 (활성화된 공지사항만)
    Page<Notice> findByIsActiveTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            String title, Pageable pageable);

    // 작성자로 검색 (활성화된 공지사항만)
    Page<Notice> findByIsActiveTrueAndAdminContainingIgnoreCaseOrderByCreatedAtDesc(
            String adminId, Pageable pageable);

    // 조회수 기준 인기 공지사항 조회
    @Query("SELECT n FROM Notice n WHERE n.isActive = true ORDER BY n.viewCount DESC, n.createdAt DESC")
    Page<Notice> findPopularNotices(Pageable pageable);
}