package nserve.delivery_application_backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import nserve.delivery_application_backend.entity.Role;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String name;
    String phone;
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    String email;
    String address;
    List<String> roles;
}
