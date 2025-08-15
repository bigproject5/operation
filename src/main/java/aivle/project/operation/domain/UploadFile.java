package aivle.project.operation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    private String fileName;
    private String savedName;
    private String fileUrl;
    private Long fileSize;

    @Column(updatable = false)
    private LocalDateTime uploadTime = LocalDateTime.now();
}
