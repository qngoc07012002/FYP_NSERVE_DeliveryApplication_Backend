package nserve.delivery_application_backend.dto.request.Food;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodUpdateRequest {
    String id;
    String name;
    String description;
    float price;
    String categoryId;
    String restaurantId;
    String imgUrl;
}