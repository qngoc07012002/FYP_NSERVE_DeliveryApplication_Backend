package nserve.delivery_application_backend.repository;

import nserve.delivery_application_backend.entity.Food;
import nserve.delivery_application_backend.entity.Restaurant;
import nserve.delivery_application_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, String> {


    List<Food> findByRestaurant(Restaurant restaurant);
}
