package nserve.delivery_application_backend.repository;

import nserve.delivery_application_backend.entity.Category;
import nserve.delivery_application_backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {
}