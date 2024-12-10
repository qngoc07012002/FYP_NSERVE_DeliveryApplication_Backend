package nserve.delivery_application_backend.controller;

import com.cloudinary.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.DistanceRequest;
import nserve.delivery_application_backend.dto.request.Order.OrderCreationRequest;
import nserve.delivery_application_backend.dto.request.PaymentRequest;
import nserve.delivery_application_backend.dto.request.UserCreationRequest;
import nserve.delivery_application_backend.dto.request.UserUpdateRequest;
import nserve.delivery_application_backend.dto.request.Websocket.OrderStatusUpdateRequest;
import nserve.delivery_application_backend.dto.request.Websocket.WebsocketRequest;
import nserve.delivery_application_backend.dto.response.*;
import nserve.delivery_application_backend.dto.response.Order.OrderCustomerResponse;
import nserve.delivery_application_backend.dto.response.Order.OrderDriverResponse;
import nserve.delivery_application_backend.dto.response.Order.OrderRestaurantResponse;
import nserve.delivery_application_backend.dto.response.Websocket.ReceiveOrderRestaurantResponse;
import nserve.delivery_application_backend.dto.response.Websocket.WebsocketResponse;
import nserve.delivery_application_backend.entity.Order;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.service.OrderService;
import nserve.delivery_application_backend.service.StripeService;
import nserve.delivery_application_backend.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;
    StripeService stripeService;

    @PostMapping("/createPaymentIntent")
    public ApiResponse<PaymentIntentResponse> createPaymentIntent(@RequestBody PaymentRequest request) {
        return ApiResponse.<PaymentIntentResponse>builder()
                .result(stripeService.createPaymentIntent(request.getAmount()))
                .build();
    }

    @GetMapping("/checkPaymentStatus/{paymentIntentId}")
    public ApiResponse<String> checkPaymentStatus(@PathVariable String paymentIntentId) {
        try {
            String status = stripeService.checkPaymentStatus(paymentIntentId);
            return ApiResponse.<String>builder()
                    .result(status)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .result("Unable to fetch payment status: " + e.getMessage())
                    .build();
        }
    }



    @GetMapping("/driver")
    public ApiResponse<List<OrderDriverResponse>> getAllOrdersForDriver() {

        List<OrderDriverResponse> orders = orderService.getOrdersForDriver();

        return ApiResponse.<List<OrderDriverResponse>>builder()
                .result(orders)
                .build();

    }

    @GetMapping("/restaurant")
    public ApiResponse<List<OrderRestaurantResponse>> getAllOrdersForRestaurant() {
        return ApiResponse.<List<OrderRestaurantResponse>>builder()
                .result(orderService.getOrdersByRestaurant())
                .build();
    }

    @GetMapping("/customer")
    public ApiResponse<List<OrderCustomerResponse>> getAllOrdersForCustomer() {
        return ApiResponse.<List<OrderCustomerResponse>>builder()
                .result(orderService.getOrdersForCustomer())
                .build();
    }


    @PostMapping("/calculate-shipping-fee")
    public ApiResponse<ShippingFeeResponse> calculateShippingFee(@RequestBody DistanceRequest distanceRequestDto) {

        return ApiResponse.<ShippingFeeResponse>builder()
                .result(orderService.calculateShippingFee(distanceRequestDto.getRestaurantId(), distanceRequestDto.getCustomerLat(), distanceRequestDto.getCustomerLng()))
                .build();
    }

    @GetMapping("/restaurant/{orderId}")
    public ApiResponse<OrderRestaurantResponse> getOrderById(@PathVariable("orderId") String orderId) {
        return ApiResponse.<OrderRestaurantResponse>builder()
                .result(orderService.getOrderById(orderId))
                .build();

    }

    @GetMapping("/driver/{orderId}")
    public ApiResponse<OrderDriverResponse> getOrderByIdDriver(@PathVariable("orderId") String orderId) {
        return ApiResponse.<OrderDriverResponse>builder()
                .result(orderService.getOrderByIdDriver(orderId))
                .build();
    }

    @GetMapping("/customer/{orderId}")
    public ApiResponse<OrderCustomerResponse> getOrderByIdCustomer(@PathVariable("orderId") String orderId) {
        return ApiResponse.<OrderCustomerResponse>builder()
                .result(orderService.getOrderByIdCustomer(orderId))
                .build();
    }

    @PostMapping("/createOrder")
    public void createOrder(@RequestBody OrderCreationRequest orderCreationRequest) {
        orderService.createOrder(orderCreationRequest);

    }

    @MessageMapping("/order/customer/createOrder")
    public void createOrderFood(OrderCreationRequest orderCreationRequest) {
        orderService.createOrder(orderCreationRequest);
        // messagingTemplate.convertAndSend("/queue/customer/order/" + "123",  order);

       // messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurant().getId(), order);

    }

    @MessageMapping("/order/restaurant/")
    public void updateRestaurantOrderStatus(OrderStatusUpdateRequest orderStatusUpdateRequest) {
        orderService.updateOrderStatus(orderStatusUpdateRequest);

       // messagingTemplate.convertAndSend("/queue/restaurant/order/" + "123",  new WebsocketResponse<String>().builder().action("REQUEST_CUSTOMER_ORDER").body(order.getId()).build());
        // messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurant().getId(), order);

    }

    @MessageMapping("/order/driver/")
    public void updateDriverOrderStatus(OrderStatusUpdateRequest orderStatusUpdateRequest) {
        orderService.updateOrderStatus(orderStatusUpdateRequest);

        // messagingTemplate.convertAndSend("/queue/restaurant/order/" + "123",  new WebsocketResponse<String>().builder().action("REQUEST_CUSTOMER_ORDER").body(order.getId()).build());
        // messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurant().getId(), order);

    }


    @GetMapping("/statistics")
    public ApiResponse<StatisticsResponse> getStatistics() {
        return ApiResponse.<StatisticsResponse>builder()
                .result(orderService.getStatistics())
                .build();
    }
}
