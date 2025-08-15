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
        private String fileName;
        private String savedName;
        private String fileUrl;
        private Long fileSize;
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
                .files(notice.getFiles().stream()
                        .map(uploadFile -> new UploadFileDto(
                                uploadFile.getFileName(),
                                uploadFile.getSavedName(),
                                uploadFile.getFileUrl(),
                                uploadFile.getFileSize()
                        )).collect(Collectors.toList())
                ).build();
    }
}