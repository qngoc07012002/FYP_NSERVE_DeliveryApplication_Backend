package nserve.delivery_application_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCAUGHT_EXCEPTION(9999, "Uncaught exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1000, "Invalid key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1003, "Username must be at least 5 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    FIRST_NAME_INVALID(1005, "First name must be at least 2 characters", HttpStatus.BAD_REQUEST),
    LAST_NAME_INVALID(1006, "Last name must be at least 2 characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "Unauthorized", HttpStatus.FORBIDDEN),
    PHONE_INVALID(1009, "Phone number is invalid", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(1010, "Category not found", HttpStatus.NOT_FOUND),
    RESTAURANT_NOT_FOUND(1011, "Restaurant not found", HttpStatus.NOT_FOUND),
    FOOD_NOT_FOUND(1012, "Food not found", HttpStatus.NOT_FOUND),
    FILE_UPLOAD_FAILED(1013, "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TYPE_INVALID(1014, "Only PNG and JPG files are allowed", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }


}
