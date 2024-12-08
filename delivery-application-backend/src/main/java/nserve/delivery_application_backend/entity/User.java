package nserve.delivery_application_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    String imgUrl;
    Date createAt;
    Date updateAt;
    @JsonIgnore
    @OneToMany
    Set<Role> roles;


}
