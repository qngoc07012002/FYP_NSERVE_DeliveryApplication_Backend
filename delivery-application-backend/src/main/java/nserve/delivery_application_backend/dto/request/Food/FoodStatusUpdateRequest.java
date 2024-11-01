package nserve.delivery_application_backend.dto.request.Food;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;

}