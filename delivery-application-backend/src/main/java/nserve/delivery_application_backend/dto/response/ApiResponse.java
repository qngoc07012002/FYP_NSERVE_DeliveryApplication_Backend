package nserve.delivery_application_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Các object null sẽ không được trả về
public class ApiResponse<T>{
    @Builder.Default
    int code = 1000;
    String message;
    T result;
}
