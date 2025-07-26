package aivle.project.operation.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
    private int code;
    private String message;
    private String request;
    private Long id;
    private String role;
}
