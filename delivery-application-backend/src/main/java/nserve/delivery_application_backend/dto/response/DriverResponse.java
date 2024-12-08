package nserve.delivery_application_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DriverResponse {

    String driverId;
    String userId;
    String driverName;
    String imgUrl;
    String status;
    float balance;
}
