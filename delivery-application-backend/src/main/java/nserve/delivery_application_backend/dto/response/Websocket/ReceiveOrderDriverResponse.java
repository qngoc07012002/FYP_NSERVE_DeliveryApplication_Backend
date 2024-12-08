package nserve.delivery_application_backend.dto.response.Websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import nserve.delivery_application_backend.entity.Location;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiveOrderDriverResponse {
    String orderId;
    String orderType;
    Location startLocation;
    Location endLocation;
    float shippingFee;
}
