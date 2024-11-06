package nserve.delivery_application_backend.controller;

import com.cloudinary.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.UserCreationRequest;
import nserve.delivery_application_backend.dto.request.UserUpdateRequest;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.Order.OrderRestaurantResponse;
import nserve.delivery_application_backend.dto.response.UserResponse;
import nserve.delivery_application_backend.entity.Order;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.service.OrderService;
import nserve.delivery_application_backend.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class OrderController {
    OrderService orderService;

    @GetMapping("/customer")
    public ApiResponse<List<Order>> getAllOrdersForCustomer() {
        return null;
    }

    @GetMapping("/driver")
    public ApiResponse<List<Order>> getAllOrdersForDriver() {
        return null;
    }

    @GetMapping("/restaurant")
    public ApiResponse<List<OrderRestaurantResponse>> getAllOrdersForRestaurant() {
        return ApiResponse.<List<OrderRestaurantResponse>>builder()
                .result(orderService.getOrdersByRestaurant())
                .build();
    }
}
