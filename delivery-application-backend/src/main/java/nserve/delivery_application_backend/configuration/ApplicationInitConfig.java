package nserve.delivery_application_backend.configuration;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.entity.Role;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.repository.RoleRepository;
import nserve.delivery_application_backend.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository){
        return args -> {
            if (userRepository.findByEmail("admin").isEmpty()) {
                Role adminRole = new Role().builder()
                        .name("ADMIN")
                        .description("Admin role").build();
                roleRepository.save(adminRole);

                Role customerRole = new Role().builder()
                        .name("CUSTOMER")
                        .description("Customer role").build();
                roleRepository.save(customerRole);

                Role restaurantRole = new Role().builder()
                        .name("RESTAURANT")
                        .description("Restaurant role").build();
                roleRepository.save(restaurantRole);

                Role driverRole = new Role().builder()
                        .name("DRIVER")
                        .description("Driver role").build();
                roleRepository.save(driverRole);

                var roles = new HashSet<Role>();
                roles.add(adminRole);
                roles.add(customerRole);
                roles.add(restaurantRole);
                roles.add(driverRole);
                User user = User.builder()
                        .email("admin")
                        .password(passwordEncoder.encode("admin"))
                         .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("Admin user created with password: admin");
            }
        };
    }
}
