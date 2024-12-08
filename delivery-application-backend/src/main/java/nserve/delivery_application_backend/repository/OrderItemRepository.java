package nserve.delivery_application_backend.repository;

import nserve.delivery_application_backend.entity.OrderItem;
import nserve.delivery_application_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    List<OrderItem> findByOrderId(String id);

    void deleteByOrderId(String orderId);
}
