package nserve.delivery_application_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import nserve.delivery_application_backend.entity.Category;
import nserve.delivery_application_backend.entity.Restaurant;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodResponse {
    String id;
    String name;
    String description;
    float price;
    String imgUrl;
    String status;
    Date createAt;
    Date updateAt;
}
