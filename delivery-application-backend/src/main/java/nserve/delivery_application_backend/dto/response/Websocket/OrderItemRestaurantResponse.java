package nserve.delivery_application_backend.dto.response.Websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import nserve.delivery_application_backend.entity.OrderItem;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemRestaurantResponse {
    String foodName;
    int quantity;
    float totalPrice;
}