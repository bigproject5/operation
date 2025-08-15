package aivle.project.operation.service;

import aivle.project.operation.domain.UploadFile;
import aivle.project.operation.domain.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // S3 사용 시 주석 해제
    // private final AmazonS3 amazonS3;
    // @Value("${cloud.aws.s3.bucket}")
    // private String bucketName;

    public List<UploadFile> uploadFile(List<MultipartFile> files) {
        // ============ 로컬 저장 방식 ============
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

        try {
            File dir = uploadDir.toFile();
            if(!dir.exists()){
                dir.mkdirs();
            }
            List<UploadFile> uploadFiles = new ArrayList<>();

            for(MultipartFile file : files){
                if(!file.isEmpty()){
                    String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
                    String savedName = UUID.randomUUID() + extension;

                    // 로컬 저장
                    Path filePath = uploadDir.resolve(savedName);
                    file.transferTo(filePath.toFile());

                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setFileName(file.getOriginalFilename());
                    uploadFile.setSavedName(savedName);
                    uploadFile.setFileUrl("/uploads/" + savedName); // 로컬 경로
                    uploadFile.setFileSize(file.getSize());
                    uploadFiles.add(uploadFile);

                    log.info("파일 저장 완료: {}/{}", filePath, file.getOriginalFilename());
                }
            }
            return uploadFiles;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        // ============ S3 저장 방식 (주석 해제 시 사용) ============
        /*
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
        */
    }

    public Resource downloadFile(Long fileId) {
        // ============ 로컬 다운로드 방식 ============
        try {
            UploadFile uploadFile = uploadFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("파일을 존재하지 않습니다."));

            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads").resolve(uploadFile.getSavedName());
            Resource resource = new UrlResource(uploadDir.toUri());

            if(!resource.exists()){
                throw new RuntimeException("파일이 존재하지 않습니다.");
            }

            return resource;
        }
        catch (Exception e){
            throw new RuntimeException("파일 로드 실패", e);
        }

        // ============ S3 다운로드 방식 (주석 해제 시 사용) ============
        /*
        try {
            UploadFile uploadFile = uploadFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("파일이 존재하지 않습니다."));

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
        */
    }

    public String getFileContentType(Resource resource) {
        try {
            String contentType = Files.probeContentType(Paths.get(resource.getURI()));
            return contentType != null ? contentType : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }

        // ============ S3용 Content-Type (주석 해제 시 사용) ============
        /*
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
        */
    }

    // ============ S3 파일 삭제 메서드 (주석 해제 시 사용) ============
    /*
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
    */
}