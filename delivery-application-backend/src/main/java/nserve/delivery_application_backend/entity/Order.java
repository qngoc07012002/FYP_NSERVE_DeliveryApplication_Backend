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
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    Restaurant restaurant;
    @ManyToOne
    @JoinColumn(name = "driver_id")
    Driver driver;
    String orderStatus;
    String orderType;
    float totalPrice;
    float shippingFee;
    Date createAt;
    String orderCode;
    @ManyToOne
    @JoinColumn(name = "start_id")
    Location startLocation;
    @ManyToOne
    @JoinColumn(name = "end_id")
    Location endLocation;
    String paymentMethod;
    String paymentIntentId;

}
