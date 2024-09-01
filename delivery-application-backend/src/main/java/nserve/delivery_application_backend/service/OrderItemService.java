package nserve.delivery_application_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.UserCreationRequest;
import nserve.delivery_application_backend.dto.request.UserUpdateRequest;
import nserve.delivery_application_backend.dto.response.UserResponse;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.enums.Role;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import nserve.delivery_application_backend.mapper.UserMapper;
import nserve.delivery_application_backend.repository.RoleRepository;
import nserve.delivery_application_backend.repository.UserRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class OrderItemService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());


        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        log.info("In method get Users");
        return userRepository.findAll();
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("User: " + user);
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());

        user.setRoles(new HashSet<>(roles));


        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
}
