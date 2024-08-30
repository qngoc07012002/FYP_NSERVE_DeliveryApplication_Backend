package nserve.delivery_application_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;
    @ManyToOne
    @JoinColumn(name = "food_id")
    Food food;
    int quantity;
    float price;
    
}
