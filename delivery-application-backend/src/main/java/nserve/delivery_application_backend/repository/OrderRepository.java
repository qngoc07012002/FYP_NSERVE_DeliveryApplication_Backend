package nserve.delivery_application_backend.repository;

import nserve.delivery_application_backend.entity.Order;
import nserve.delivery_application_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {


}
