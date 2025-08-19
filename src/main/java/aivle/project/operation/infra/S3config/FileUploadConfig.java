package aivle.project.operation.infra.S3config;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FileUploadConfig {

    // 허용된 파일 확장자 (화이트리스트)
    public static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp",  // 이미지
            "pdf", "doc", "docx", "hwp",         // 문서
            "txt", "csv", "xlsx", "xls",         // 데이터
            "ppt", "pptx", "zip", "rar"          // 기타
    );

    // 허용된 MIME 타입
    public static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/bmp",
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/haansofthwp", "text/plain", "text/csv",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/zip", "application/x-rar-compressed"
    );

    // 파일 크기 제한 (10MB)
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
}