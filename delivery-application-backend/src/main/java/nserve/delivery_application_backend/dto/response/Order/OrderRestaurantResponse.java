package nserve.delivery_application_backend.dto.response.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderRestaurantResponse {
    String id;
    String orderCode;
    String customerName;
    String driverName;
    String driverImgUrl;
    String startLocation;
    String endLocation;
    float totalPrice;
    String orderStatus;
    Date createAt;
    List<OrderItemResponse> orderItems;
}
