package aivle.project.operation.domain.dto;


import aivle.project.operation.domain.UploadFile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
public class FileResponseDto {
    private Long fileId;
    private String fileName;
    private Long fileSize;


    public static FileResponseDto from(UploadFile uploadFile) {
        return FileResponseDto.builder()
                .fileId(uploadFile.getId())
                .fileName(uploadFile.getFileName())
                .fileSize(uploadFile.getFileSize())
                .build();
    }
}