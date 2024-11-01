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
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name ="owner_id")
    User owner;
    String restaurantName;
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    String description;
    String address;
    float rating;
    String imgUrl;

    @ManyToOne
    @JoinColumn(name ="location_id")
    Location location;
    String status;
}
