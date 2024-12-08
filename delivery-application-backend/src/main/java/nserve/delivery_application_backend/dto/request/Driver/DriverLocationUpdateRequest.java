package nserve.delivery_application_backend.dto.request.Driver;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverLocationUpdateRequest {
    float latitude;
    float longitude;
}
