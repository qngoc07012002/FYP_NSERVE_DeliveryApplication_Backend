package nserve.delivery_application_backend.dto.response.Restaurant;

import lombok.*;
import lombok.experimental.FieldDefaults;
import nserve.delivery_application_backend.entity.Category;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantResponse {
    String id;
    String restaurantName;
    String description;
    String address;
    String imgUrl;
    String status;
    Category category;
    float rating;
}
