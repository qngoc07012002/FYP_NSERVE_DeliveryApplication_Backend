package nserve.delivery_application_backend.dto.request.Order;

import lombok.*;
import lombok.experimental.FieldDefaults;
import nserve.delivery_application_backend.entity.Location;
import nserve.delivery_application_backend.entity.OrderItem;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {
    String restaurantId;
    Location customerLocation;
    List<OrderItem> orderItems;
    float shippingFee;
    float distance;
    float totalPrice;
    String orderType;
    String paymentMethod;
    String paymentIntentId;
}
