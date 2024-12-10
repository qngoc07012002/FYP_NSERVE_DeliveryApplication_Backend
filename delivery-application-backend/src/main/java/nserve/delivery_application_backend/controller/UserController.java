package nserve.delivery_application_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.*;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.UserResponse;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.service.AuthenticationService;
import nserve.delivery_application_backend.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class UserController {
    UserService userService;
    AuthenticationService authenticationService;

    @PostMapping()
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.createUser(request));

        return apiResponse;
    }

    @GetMapping()
    List<User> getAllUsers() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("User: {}", authentication.getName());
        authentication.getAuthorities().forEach(authority -> log.info("Authority: {}", authority.getAuthority()));

        return userService.getAllUsers();
    }

    @GetMapping("/info")
    ApiResponse<UserResponse> getMyInfo() {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.getMyInfo());

        return apiResponse;
    }

    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId) {
        return userService.getUser(userId);
    }

//    @PutMapping("/{userId}")
//    UserResponse updateUser(@PathVariable("userId") String userId ,@RequestBody UserUpdateRequest request) {
//        return userService.updateUser(userId, request);
//    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return "User deleted";
    }

    @PostMapping("/registerCustomer")
    ApiResponse<String> registerCustomer(@RequestBody RegisterCustomerRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();

        apiResponse.setResult(authenticationService.registerCustomer(request));

        return apiResponse;
    }

    @PostMapping("/registerDriver")
    ApiResponse<String> registerDriver(@RequestBody RegisterDriverRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();

        apiResponse.setResult(authenticationService.registerDriver(request));

        return apiResponse;
    }

    @PostMapping("/registerRestaurant")
    ApiResponse<String> registerRestaurant(@RequestBody RegisterRestaurantRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();

        apiResponse.setResult(authenticationService.registerRestaurant(request));

        return apiResponse;
    }

    @PostMapping("/updateCustomer")
    ApiResponse<String> updateCustomer(@RequestBody UpdateCustomerRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.updateCustomer(request));

        return apiResponse;
    }

    @PostMapping("/updateDriver")
    ApiResponse<String> updateCustomer(@RequestBody UpdateDriverRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.updateDriver(request));

        return apiResponse;
    }

    @PostMapping("/updateRestaurant")
    ApiResponse<String> updateCustomer(@RequestBody UpdateRestaurantRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.updateRestaurant(request));

        return apiResponse;
    }
}
