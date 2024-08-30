package nserve.delivery_application_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String phoneNumber;
    String email;
    String password;
    String fullName;
    String regionId;
    Date createAt;
    Date updateAt;
    @OneToMany
    Set<Role> roles;


}
