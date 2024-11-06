package nserve.delivery_application_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.response.Order.OrderItemResponse;
import nserve.delivery_application_backend.dto.response.Order.OrderRestaurantResponse;
import nserve.delivery_application_backend.entity.Order;
import nserve.delivery_application_backend.entity.Restaurant;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import nserve.delivery_application_backend.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderItemService orderItemService;

    public List<OrderRestaurantResponse> getOrdersByRestaurant() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));


        Restaurant restaurant = restaurantRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));


        List<Order> orders = orderRepository.findByRestaurant(restaurant);

        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderRestaurantResponse convertToDTO(Order order) {
        OrderRestaurantResponse dto = new OrderRestaurantResponse();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setCustomerName(order.getUser().getFullName());
        dto.setDriverName(order.getDriver() != null ? order.getDriver().getUser().getFullName() : null);
        dto.setDriverImgUrl(order.getDriver() != null ? order.getDriver().getUser().getImgUrl() : null);
        dto.setStartLocation(order.getStartLocation().getAddress());
        dto.setEndLocation(order.getEndLocation().getAddress());
        dto.setCreateAt(order.getCreateAt());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderStatus(order.getOrderStatus());

        List<OrderItemResponse> orderItems = orderItemService.findByOrderId(order.getId()).stream()
                .map(item -> {
                    OrderItemResponse itemDto = new OrderItemResponse();
                    itemDto.setFoodId(item.getFood().getId());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setTotalPrice(item.getPrice());
                    itemDto.setFoodName(item.getFood().getName());
                    return itemDto;
                })
                .toList();


        dto.setOrderItems(orderItems);
        return dto;
    }
}
