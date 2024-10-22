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
    Category category;
    Restaurant restaurant;
    String name;
    String description;
    float price;
    String imgUrl;
    Date createAt;
    Date updateAt;
}
