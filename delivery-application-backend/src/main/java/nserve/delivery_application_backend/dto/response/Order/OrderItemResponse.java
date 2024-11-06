package nserve.delivery_application_backend.dto.response.Order;

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
public class OrderItemResponse {
    String foodId;
    String foodName; // Có thể thêm thuộc tính này nếu cần
    int quantity;
    float totalPrice;
}
