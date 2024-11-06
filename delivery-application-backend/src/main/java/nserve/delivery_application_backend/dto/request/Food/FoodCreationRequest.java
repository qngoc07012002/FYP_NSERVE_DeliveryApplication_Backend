package nserve.delivery_application_backend.dto.request.Food;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodCreationRequest {
    String name;
    String description;
    String imgUrl;
    float price;
}
