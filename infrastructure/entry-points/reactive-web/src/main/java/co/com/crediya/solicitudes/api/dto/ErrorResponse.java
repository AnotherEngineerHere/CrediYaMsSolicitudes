package co.com.crediya.solicitudes.api.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class ErrorResponse {
    String code;
    String message;
    OffsetDateTime timestamp;

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
