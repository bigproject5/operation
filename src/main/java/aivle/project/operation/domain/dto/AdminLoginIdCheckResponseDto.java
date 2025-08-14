package aivle.project.operation.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLoginIdCheckResponseDto {
    private boolean available;
    private String message;

    public AdminLoginIdCheckResponseDto setAvailable(boolean available) {
        this.available = available;
        return this;
    }

    public AdminLoginIdCheckResponseDto setMessage(String message) {
        this.message = message;
        return this;
    }
}
