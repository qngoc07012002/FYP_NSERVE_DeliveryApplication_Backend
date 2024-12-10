package nserve.delivery_application_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import nserve.delivery_application_backend.entity.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RegisterRestaurantRequest {
    String phoneNumber;
    String restaurantName;
    String description;
    Location location;
    String category;
    String imgUrl;
}
