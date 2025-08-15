package aivle.project.operation.domain.dto;

import aivle.project.operation.domain.Notice;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailResponseDto {
    private Long id;
    private String title;
    private String content;
    private Long adminId;
    private String name;
    private Integer viewCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<UploadFileDto> files;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadFileDto {
        private Long fileId;
        private String fileName;
        private String savedName;
        private String fileUrl;
        private Long fileSize;
    }

    // Entity to DTO 변환 정적 메서드
    public static NoticeDetailResponseDto from(Notice notice) {
        NoticeDetailResponseDto dto = new NoticeDetailResponseDto();

        dto.id = notice.getId();
        dto.title = notice.getTitle();
        dto.content = notice.getContent();
        dto.adminId = notice.getAdminId();
        dto.name = notice.getName();
        dto.viewCount = notice.getViewCount();
        dto.isActive = notice.getIsActive();
        dto.createdAt = notice.getCreatedAt();
        dto.updatedAt = notice.getUpdatedAt();

        dto.files = notice.getFiles().stream().map(file -> new UploadFileDto(
                file.getId(),
                file.getFileName(),
                file.getSavedName(),
                file.getFileUrl(),
                file.getFileSize()
        )).collect(Collectors.toList());

        return dto;
    }
}