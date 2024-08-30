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
    Cateog
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    Restaurant restaurant;
    String name;
    String description;
    float price;



}
