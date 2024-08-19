package nserve.delivery_application_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String address;


}
