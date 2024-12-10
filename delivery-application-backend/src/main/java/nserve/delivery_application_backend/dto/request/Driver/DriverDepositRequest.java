package nserve.delivery_application_backend.dto.request.Driver;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverDepositRequest {
    float amount;
    String paymentIntentId;
}
