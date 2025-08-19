package aivle.project.operation.service;

import aivle.project.operation.domain.UploadFile;
import aivle.project.operation.domain.UploadFileRepository;
import aivle.project.operation.infra.S3config.FileUploadConfig;
import aivle.project.operation.infra.exception.FileUploadException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final UploadFileRepository uploadFileRepository;
    private final FileUploadConfig fileUploadConfig;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<UploadFile> uploadFile(List<MultipartFile> files) {
        List<UploadFile> uploadFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                validateFile(file); // 파일 유효성 검사
                try {
                    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
                    String savedName = UUID.randomUUID() + "." + extension;
                    String keyName = "uploads/" + savedName; // S3 키

                    // S3 업로드
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(file.getSize());
                    metadata.setContentType(file.getContentType());

                    PutObjectRequest putRequest = new PutObjectRequest(bucketName, keyName, file.getInputStream(), metadata);
                    amazonS3.putObject(putRequest);

                    // S3 URL 생성
                    String s3Url = amazonS3.getUrl(bucketName, keyName).toString();

                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setFileName(file.getOriginalFilename());
                    uploadFile.setSavedName(savedName);
                    uploadFile.setFileUrl(s3Url); // S3 URL
                    uploadFile.setFileSize(file.getSize());
                    uploadFile.setContentType(file.getContentType());
                    uploadFiles.add(uploadFile);

                    log.info("S3 파일 업로드 완료: {}", s3Url);
                } catch (Exception e) {
                    log.error("S3 파일 업로드 실패: {}", file.getOriginalFilename(), e);
                    throw new FileUploadException("S3 파일 업로드 실패", e);
                }
            }
        }
        return uploadFiles;
    }

    private void validateFile(MultipartFile file) {
        // 파일 크기 검사
        if (file.getSize() > FileUploadConfig.MAX_FILE_SIZE) {
            throw new FileUploadException("파일 크기가 너무 큽니다. 최대 " + FileUploadConfig.MAX_FILE_SIZE / (1024 * 1024) + "MB까지 허용됩니다.");
        }

        // 파일 확장자 검사
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null || !FileUploadConfig.ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileUploadException("허용되지 않는 파일 확장자입니다. (허용: " + FileUploadConfig.ALLOWED_EXTENSIONS + ")");
        }

        // MIME 타입 검사
        String mimeType = file.getContentType();
        if (mimeType == null || !FileUploadConfig.ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new FileUploadException("허용되지 않는 파일 형식입니다. (허용: " + FileUploadConfig.ALLOWED_MIME_TYPES + ")");
        }
    }

    public Resource downloadFile(Long fileId) {
        try {
            UploadFile uploadFile = uploadFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("파일을 존재하지 않습니다."));

            String keyName = "uploads/" + uploadFile.getSavedName();

            // S3에서 파일 존재 확인
            if (!amazonS3.doesObjectExist(bucketName, keyName)) {
                throw new RuntimeException("S3에 파일이 존재하지 않습니다.");
            }

            // S3 객체를 InputStream으로 가져와서 Resource로 변환
            S3Object s3Object = amazonS3.getObject(bucketName, keyName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            // InputStreamResource로 반환 (또는 임시 파일 생성 후 UrlResource 사용)
            return new InputStreamResource(inputStream);

        } catch (Exception e) {
            log.error("S3 파일 다운로드 실패: fileId={}", fileId, e);
            throw new RuntimeException("S3 파일 다운로드 실패", e);
        }
    }

    public void deleteS3File(String savedName) {
        try {
            String keyName = "uploads/" + savedName;
            amazonS3.deleteObject(bucketName, keyName);
            log.info("S3 파일 삭제 완료: {}", keyName);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", savedName, e);
            throw new RuntimeException("S3 파일 삭제 실패", e);
        }
    }
}
