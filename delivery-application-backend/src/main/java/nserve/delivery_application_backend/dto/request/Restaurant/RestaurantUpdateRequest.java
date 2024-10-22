package nserve.delivery_application_backend.dto.request.Restaurant;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantUpdateRequest {
    String restaurantName;
    String address;
    float rating;
}
