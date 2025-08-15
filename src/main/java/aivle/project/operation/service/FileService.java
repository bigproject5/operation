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

    public List<UploadFile> uploadFile(List<MultipartFile> files) {
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

                    Path filePath = uploadDir.resolve(savedName);
                    file.transferTo(filePath.toFile());

                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setFileName(file.getOriginalFilename());
                    uploadFile.setSavedName(savedName);
                    uploadFile.setFileUrl("/uploads/" + savedName);
                    uploadFile.setFileSize(file.getSize());
                    uploadFiles.add(uploadFile);

                    log.info("파일 저장 완료: {}/{}", filePath, file.getOriginalFilename());
                }
            }
            return uploadFiles;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    public Resource downloadFile(Long fileId) {
        try {
            UploadFile uploadFile = uploadFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("파일을 존재하지 않습니다."));

            Path uploadDir = Paths.get("uploads").resolve(uploadFile.getSavedName());
            Resource resource = new UrlResource(uploadDir.toUri());

            if(!resource.exists()){
                throw new RuntimeException("파일이 존재하지 않습니다.");
            }

            return resource;
        }
        catch (Exception e){
            throw new RuntimeException("파일 로드 실패", e);
        }
    }

    public String getFileContentType(Resource resource) {
        try {
            String contentType = Files.probeContentType(Paths.get(resource.getURI()));
            return contentType != null ? contentType : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

}
