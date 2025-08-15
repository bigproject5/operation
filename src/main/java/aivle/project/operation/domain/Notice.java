package aivle.project.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notices")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 아이디
    @Column(nullable = false, length = 50)
    private Long adminId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadFile> files = new ArrayList<>();

    // 조회수 증가 메서드
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 공지사항 수정 메서드
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 공지사항 비활성화 메서드
    public void deactivate() {
        this.isActive = false;
    }
}