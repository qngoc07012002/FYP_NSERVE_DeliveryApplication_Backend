package nserve.delivery_application_backend.dto.request.Restaurant;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;

}