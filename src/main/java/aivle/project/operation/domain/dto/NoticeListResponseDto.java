package aivle.project.operation.domain.dto;

import aivle.project.operation.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeListResponseDto {
    private Long id;
    private String title;
    private Long adminId;
    private String name;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean hasFiles;

    // Entity to DTO 변환 정적 메서드
    public static NoticeListResponseDto from(Notice notice) {
        return NoticeListResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .adminId(notice.getAdminId())
                .name(notice.getName())
                .viewCount(notice.getViewCount())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .hasFiles(notice.getFiles() != null && !notice.getFiles().isEmpty())
                .build();
    }
}

