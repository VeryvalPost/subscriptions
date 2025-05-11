package work.subscriptions.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDTO {
    private String message;
    private String details;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String message, String details) {
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
