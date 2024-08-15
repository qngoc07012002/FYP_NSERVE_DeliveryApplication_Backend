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
                Role role = new Role().builder()
                        .name("ADMIN")
                        .description("Admin role").build();
                roleRepository.save(role);

                var roles = new HashSet<Role>();
                roles.add(role);
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
