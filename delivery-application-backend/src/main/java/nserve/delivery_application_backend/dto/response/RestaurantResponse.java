package nserve.delivery_application_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import nserve.delivery_application_backend.entity.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantResponse {
    String id;
    User owner;
    String restaurantName;
    String description;
    String address;
    String imgUrl;
    float rating;
}
