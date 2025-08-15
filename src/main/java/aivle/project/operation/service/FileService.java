package aivle.project.operation.service;

import aivle.project.operation.domain.UploadFile;
import aivle.project.operation.domain.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

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
}
