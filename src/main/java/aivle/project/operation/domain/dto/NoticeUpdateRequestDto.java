package aivle.project.operation.domain.dto;

// 공지사항 수정 시, 추가할 파일 ID 리스트와 삭제할 기존 파일 ID 리스트를 받도록 함

import aivle.project.operation.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateRequestDto {
    private Long id;
    private String title;
    private String content;
    private Long adminId;
    private String name;
    private Integer viewCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Long> removeFileIds; //삭제할 기본 파일 id

    public List<Long> getRemoveFileIds() {
        return removeFileIds != null ? removeFileIds : new ArrayList<>();
    }

    // Entity to DTO 변환 정적 메서드
    public static NoticeDetailResponseDto from(Notice notice) {
        return NoticeDetailResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .adminId(notice.getAdminId())
                .name(notice.getName())
                .viewCount(notice.getViewCount())
                .isActive(notice.getIsActive())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}