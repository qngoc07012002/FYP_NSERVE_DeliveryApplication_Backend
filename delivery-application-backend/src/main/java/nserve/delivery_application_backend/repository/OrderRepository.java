package nserve.delivery_application_backend.repository;

import nserve.delivery_application_backend.entity.Driver;
import nserve.delivery_application_backend.entity.Order;
import nserve.delivery_application_backend.entity.Restaurant;
import nserve.delivery_application_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByRestaurant(Restaurant restaurant);

    List<Order> findByDriver(Driver driver);

    List<Order> findByUser(User user);
}
