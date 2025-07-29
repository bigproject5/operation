package aivle.project.operation.domain.dto;


import aivle.project.operation.domain.Notice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCreateRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;



    // DTO to Entity 변환 메서드
    public Notice toEntity(Long adminId, String name) {
        return Notice.builder()
                .title(this.title)
                .content(this.content)
                .name(name)
                .adminId(adminId)
                .build();
    }
}