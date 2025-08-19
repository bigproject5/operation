package aivle.project.operation.service;

import aivle.project.operation.domain.UploadFile;
import aivle.project.operation.domain.UploadFileRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final UploadFileRepository uploadFileRepository;

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<UploadFile> uploadFile(List<MultipartFile> files) {
        List<UploadFile> uploadFiles = new ArrayList<>();

        for(MultipartFile file : files){
            if(!file.isEmpty()){
                try {
                    String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
                    String savedName = UUID.randomUUID() + extension;
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
                    uploadFiles.add(uploadFile);

                    log.info("S3 파일 업로드 완료: {}", s3Url);
                } catch (Exception e) {
                    log.error("S3 파일 업로드 실패: {}", file.getOriginalFilename(), e);
                    throw new RuntimeException("S3 파일 업로드 실패", e);
                }
            }
        }
        return uploadFiles;
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

    public String getFileContentType(Resource resource) {
        // S3에서는 업로드 시 저장된 메타데이터를 사용하거나
        // 파일 확장자로 추론
        try {
            if (resource instanceof InputStreamResource) {
                // 파일 확장자로 Content-Type 추론
                String filename = resource.getFilename();
                if (filename != null && filename.contains(".")) {
                    String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
                    switch (extension) {
                        case ".pdf": return "application/pdf";
                        case ".jpg":
                        case ".jpeg": return "image/jpeg";
                        case ".png": return "image/png";
                        case ".txt": return "text/plain";
                        case ".doc": return "application/msword";
                        case ".docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                        default: return "application/octet-stream";
                    }
                }
            }
            return "application/octet-stream";
        } catch (Exception e) {
            return "application/octet-stream";
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
