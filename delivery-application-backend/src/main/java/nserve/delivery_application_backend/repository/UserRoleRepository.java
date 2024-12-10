package nserve.delivery_application_backend.repository;

import nserve.delivery_application_backend.entity.Role;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    List<UserRole> findByUser(User user);
}
