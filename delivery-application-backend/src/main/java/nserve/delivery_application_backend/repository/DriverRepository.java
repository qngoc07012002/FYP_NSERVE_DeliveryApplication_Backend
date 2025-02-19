package nserve.delivery_application_backend.repository;

import nserve.delivery_application_backend.entity.Driver;
import nserve.delivery_application_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {

    Optional<Driver> findByUserId(String userId);
}
