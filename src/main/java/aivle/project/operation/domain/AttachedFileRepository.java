package aivle.project.operation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachedFileRepository extends JpaRepository<AttachedFile, Long> {
    List<AttachedFile> findByNotice(Notice notice);
    List<AttachedFile> findByNoticeId(Long noticeId);
}