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
    String name;
    String description;
    String imgUrl;
    float price;
}


