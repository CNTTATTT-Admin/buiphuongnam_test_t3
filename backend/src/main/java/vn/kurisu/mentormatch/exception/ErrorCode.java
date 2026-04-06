package vn.kurisu.mentormatch.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid message key enum", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "Username already existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username is invalid or missing", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    FULL_NAME_INVALID(1005, "Full name cannot be blank", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(1009, "Role not found", HttpStatus.NOT_FOUND),
    BAD_CREDENTIALS(1010, "Invalid username or password", HttpStatus.UNAUTHORIZED),
    TIME_SLOT_NOT_FOUND(1011, "Time slot not found", HttpStatus.NOT_FOUND),
    TIME_SLOT_CONFLICT(1012, "Time slot conflict", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID(1013, "Refresh token is invalid", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(1014, "Refresh token has expired", HttpStatus.UNAUTHORIZED),
    CHAT_MESSAGE_EMPTY(1015, "Chat message content cannot be blank", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
