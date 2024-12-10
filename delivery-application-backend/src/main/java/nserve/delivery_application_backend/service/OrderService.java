package nserve.delivery_application_backend.service;

import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.Order.OrderCreationRequest;
import nserve.delivery_application_backend.dto.request.Websocket.OrderStatusUpdateRequest;
import nserve.delivery_application_backend.dto.response.Order.OrderCustomerResponse;
import nserve.delivery_application_backend.dto.response.Order.OrderDriverResponse;
import nserve.delivery_application_backend.dto.response.Order.OrderItemResponse;
import nserve.delivery_application_backend.dto.response.Order.OrderRestaurantResponse;
import nserve.delivery_application_backend.dto.response.ShippingFeeResponse;
import nserve.delivery_application_backend.dto.response.Websocket.OrderItemRestaurantResponse;
import nserve.delivery_application_backend.dto.response.Websocket.ReceiveOrderDriverResponse;
import nserve.delivery_application_backend.dto.response.Websocket.ReceiveOrderRestaurantResponse;
import nserve.delivery_application_backend.dto.response.Websocket.WebsocketResponse;
import nserve.delivery_application_backend.entity.*;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import nserve.delivery_application_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
    private final LocationRepository locationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderItemRepository orderItemRepository;
    private final DriverRepository driverRepository;
    StripeService stripeService;

    @Autowired
    private StringRedisTemplate redisTemplate;


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

    public OrderRestaurantResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return convertToDTO(order);
    }


    public OrderDriverResponse getOrderByIdDriver(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return OrderDriverResponse.builder()
                .orderCode(order.getOrderCode())
                .orderType(order.getOrderType())
                .restaurantInfo(
                        OrderDriverResponse.RestaurantInfo.builder()
                                .name(order.getRestaurant().getRestaurantName())
                                .img(order.getRestaurant().getImgUrl())
                                .restaurantLocation(order.getRestaurant().getAddress())
                                .build()
                )
                .userInfo(
                        OrderDriverResponse.UserInfo.builder()
                                .name(order.getUser().getFullName())
                                .img(order.getUser().getImgUrl())
                                .build()
                )
                .startLocation(order.getStartLocation())
                .endLocation(order.getEndLocation())
                .orderStatus(order.getOrderStatus())
                .shippingFee(order.getShippingFee())
                .createdAt(order.getCreateAt())
                .build();
    }

    public List<OrderDriverResponse> getOrdersForDriver() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));


        List<Order> orders = orderRepository.findByDriver(driver);

        return orders.stream().map(order -> {
            return OrderDriverResponse.builder()
                    .id(order.getId())
                    .orderCode(order.getOrderCode())
                    .orderType(order.getOrderType())
                    .restaurantInfo(
                            OrderDriverResponse.RestaurantInfo.builder()
                                    .name(order.getRestaurant().getRestaurantName())
                                    .img(order.getRestaurant().getImgUrl())
                                    .restaurantLocation(order.getRestaurant().getAddress())
                                    .build()
                    )
                    .userInfo(
                            OrderDriverResponse.UserInfo.builder()
                                    .name(order.getUser().getFullName())
                                    .img(order.getUser().getImgUrl())
                                    .build()
                    )
                    .startLocation(order.getStartLocation())
                    .endLocation(order.getEndLocation())
                    .orderStatus(order.getOrderStatus())
                    .shippingFee(order.getShippingFee())
                    .createdAt(order.getCreateAt())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<OrderCustomerResponse> getOrdersForCustomer() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream().map(order -> {
            return OrderCustomerResponse.builder()
                    .id(order.getId())
                    .orderCode(order.getOrderCode())
                    .orderType(order.getOrderType())
                    .restaurantInfo(
                            OrderCustomerResponse.RestaurantInfo.builder()
                                    .name(order.getRestaurant().getRestaurantName())
                                    .img(order.getRestaurant().getImgUrl())
                                    .restaurantLocation(order.getRestaurant().getAddress())
                                    .build()
                    )
                    .driverInfo(
                            OrderCustomerResponse.DriverInfo.builder()
                                    .name(order.getDriver() != null ? order.getDriver().getUser().getFullName() : null)
                                    .img(order.getDriver() != null ? order.getDriver().getUser().getImgUrl() : null)
                                    .phoneNumber(order.getDriver() != null ? order.getDriver().getUser().getPhoneNumber() : null)
                                    .build()
                    )
                    .startLocation(order.getStartLocation())
                    .endLocation(order.getEndLocation())
                    .orderStatus(order.getOrderStatus())
                    .shippingFee(order.getShippingFee())
                    .totalPrice(order.getTotalPrice())
                    .createdAt(order.getCreateAt())
                    .orderItems(orderItemService.findByOrderId(order.getId()).stream()
                            .map(item -> OrderItemResponse.builder()
                                    .foodId(item.getId())
                                    .foodName(item.getFoodName())
                                    .quantity(item.getQuantity())
                                    .totalPrice(item.getTotalPrice())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());


    }

    public OrderCustomerResponse getOrderByIdCustomer(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return OrderCustomerResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .orderType(order.getOrderType())
                .restaurantInfo(
                        OrderCustomerResponse.RestaurantInfo.builder()
                                .name(order.getRestaurant().getRestaurantName())
                                .img(order.getRestaurant().getImgUrl())
                                .restaurantLocation(order.getRestaurant().getAddress())
                                .build()
                )
                .driverInfo(
                        OrderCustomerResponse.DriverInfo.builder()
                                .name(order.getDriver() != null ? order.getDriver().getUser().getFullName() : null)
                                .img(order.getDriver() != null ? order.getDriver().getUser().getImgUrl() : null)
                                .phoneNumber(order.getDriver() != null ? order.getDriver().getUser().getPhoneNumber() : null)
                                .build()
                )
                .startLocation(order.getStartLocation())
                .endLocation(order.getEndLocation())
                .orderStatus(order.getOrderStatus())
                .shippingFee(order.getShippingFee())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreateAt())
                .orderItems(orderItemService.findByOrderId(order.getId()).stream()
                        .map(item -> OrderItemResponse.builder()
                                .foodId(item.getId())
                                .foodName(item.getFoodName())
                                .quantity(item.getQuantity())
                                .totalPrice(item.getTotalPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
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
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setTotalPrice(item.getTotalPrice());
                    itemDto.setFoodName(item.getFoodName());
                    return itemDto;
                })
                .toList();


        dto.setOrderItems(orderItems);
        return dto;
    }

    public ShippingFeeResponse calculateShippingFee(String restaurantId, double customerLat, double customerLng) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        double distance = calculateDistance(restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude(), customerLat, customerLng);
        double shippingFee = calculateShippingFeeFromDistance(distance);

        return ShippingFeeResponse.builder()
                .distance(distance)
                .shippingFee(shippingFee)
                .build();
    }


    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        final double EARTH_RADIUS_KM = 6371.0;

        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return roundToTwoDecimalPlaces(EARTH_RADIUS_KM * c);
    }

    //0.5$ per km
    public double calculateShippingFeeFromDistance(double distance) {
        final double FEE_PER_KM = 0.5;
        return roundToTwoDecimalPlaces(distance * FEE_PER_KM);
    }

    private double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void createOrder(OrderCreationRequest orderCreationRequest) {
        if (orderCreationRequest.getOrderType().equals("FOOD")) {
            Restaurant restaurant = restaurantRepository.findById(orderCreationRequest.getRestaurantId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

            if (!checkRestaurantAvailable(orderCreationRequest.getRestaurantId())) {
                throw new AppException(ErrorCode.RESTAURANT_OFFLINE);
            }

            if (orderCreationRequest.getPaymentMethod().equals("Credit")){
                if (orderCreationRequest.getPaymentIntentId().isEmpty()){
                    log.error("Payment intent is required");
                    throw new AppException(ErrorCode.PAYMENT_INTENT_REQUIRED);
                } else {
                    try {
                        if (!stripeService.checkPaymentAmount(orderCreationRequest.getPaymentIntentId(), orderCreationRequest.getTotalPrice() + orderCreationRequest.getShippingFee() )) {
                            log.error("Payment failed");
                            throw new AppException(ErrorCode.PAYMENT_INTENT_REQUIRED);
                        }
                    } catch (StripeException e) {
                        log.error("Payment failed");
                        throw new AppException(ErrorCode.PAYMENT_INTENT_REQUIRED);
                    }

                }

            }

            var context = SecurityContextHolder.getContext();
            String userId = context.getAuthentication().getName();

            Order order = orderRepository.save(Order.builder()
                    .createAt(Date.from(java.time.Instant.now()))
                    .restaurant(restaurant)
                    .user(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))
                    )
                    .orderStatus("PENDING")
                    .orderCode(generateOrderCode(orderCreationRequest.getOrderType()))
                    .orderType(orderCreationRequest.getOrderType())
                    .paymentMethod(orderCreationRequest.getPaymentMethod())
                    .paymentIntentId(orderCreationRequest.getPaymentIntentId())
                    .totalPrice(orderCreationRequest.getTotalPrice())
                    .shippingFee(orderCreationRequest.getShippingFee())
                    .startLocation(locationRepository.save(restaurant.getLocation()))
                    .endLocation(locationRepository.save(orderCreationRequest.getCustomerLocation()))
                    .build());
            orderItemService.createOrderItems(orderCreationRequest.getOrderItems(), order);
            List<OrderItem> orderItems = orderCreationRequest.getOrderItems();

            sendOrderToRestaurant(order.getId(), order.getRestaurant().getId(), orderItems.stream().map(item -> OrderItemRestaurantResponse.builder()
                    .foodName(item.getFoodName())
                    .quantity(item.getQuantity())
                    .totalPrice(item.getTotalPrice())
                    .build()).collect(Collectors.toList()));
        }

    }

    private void sendOrderToRestaurant(String orderId, String restaurantId, List<OrderItemRestaurantResponse> orderItems)
    {   log.info("Sending order to restaurant");
        messagingTemplate.convertAndSend("/queue/restaurant/order/" + restaurantId,  WebsocketResponse.builder().action("CUSTOMER_REQUEST_ORDER").body(
                ReceiveOrderRestaurantResponse.builder()
                        .orderId(orderId)
                        .orderItems(orderItems)
                        .build()
        ).build());
    }

    private boolean checkRestaurantAvailable(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (restaurant.getStatus().equals("OFFLINE")) {
            log.info("Restaurant is offline");
            return false;
        }

        return true;
    }

    @Transactional
    public void updateOrderStatus(OrderStatusUpdateRequest orderStatusUpdateRequest) {
        Order order = orderRepository.findById(orderStatusUpdateRequest.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getOrderStatus().equals("CANCELLED")) {
            orderRepository.delete(order);
            log.info("Order is cancelled");

            return;
        }

        if (orderStatusUpdateRequest.getAction().equals("RESTAURANT_DECLINE_ORDER")) {
            messagingTemplate.convertAndSend("/queue/customer/order/" + order.getUser().getId(),
                    WebsocketResponse.builder().action("RESTAURANT_DECLINE_ORDER").body(
                            ReceiveOrderRestaurantResponse.builder()
                                    .orderId(order.getId())
                                    .build()
                    ).build());

            if (order.getDriver() != null){
                messagingTemplate.convertAndSend("/queue/driver/order/" + order.getDriver().getId(),
                        WebsocketResponse.builder().action("RESTAURANT_DECLINE_ORDER").body(
                                ReceiveOrderRestaurantResponse.builder()
                                        .orderId(order.getId())
                                        .build()
                        ).build());

            }


            cancelOrder(order.getId());
            order.setOrderStatus("CANCELED");
            orderRepository.save(order);
            log.info("Order is declined");

        } else if (orderStatusUpdateRequest.getAction().equals("RESTAURANT_ACCEPT_ORDER")) {
            order.setOrderStatus("FINDING_DRIVER");
            orderRepository.save(order);
            log.info("Order is accepted");

            messagingTemplate.convertAndSend("/queue/customer/order/" + order.getUser().getId(),  WebsocketResponse.builder().action("RESTAURANT_ACCEPT_ORDER").body(
                    ReceiveOrderRestaurantResponse.builder()
                            .orderId(order.getId())
                            .build()
            ).build());

        }

        if (orderStatusUpdateRequest.getAction().equals("DRIVER_ACCEPT_ORDER")) {
            log.info("Driver accepted order: " + orderStatusUpdateRequest.getOrderId());
            log.info("DRIVER INFO: " + orderStatusUpdateRequest.getDriverId());

            Driver driver = driverRepository.findById(orderStatusUpdateRequest.getDriverId())
                    .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));

            order.setDriver(driver);

            order.setOrderStatus("PREPARING");

            orderRepository.save(order);

            messagingTemplate.convertAndSend("/queue/restaurant/order/" + order.getRestaurant().getId(),
                    WebsocketResponse.builder().action("DRIVER_ACCEPT_ORDER").body(
                    ReceiveOrderRestaurantResponse.builder()
                            .orderId(order.getId())
                            .build()
            ).build());

            messagingTemplate.convertAndSend("/queue/customer/order/" + order.getUser().getId(),
                    WebsocketResponse.builder().action("DRIVER_ACCEPT_ORDER").body(
                    ReceiveOrderRestaurantResponse.builder()
                            .orderId(order.getId())
                            .build()
            ).build());

        }

        if (orderStatusUpdateRequest.getAction().equals("DRIVER_DECLINE_ORDER")) {
            log.info("Order is declined by driver");

            String blacklistKey = "order:blacklist:" + orderStatusUpdateRequest.getOrderId();
            redisTemplate.opsForSet().add(blacklistKey, orderStatusUpdateRequest.getDriverId());

            log.info("Driver {} added to blacklist for Order {}", orderStatusUpdateRequest.getDriverId(), order.getId());

            findDriver(orderStatusUpdateRequest.getOrderId());
        }

        if (order.getOrderStatus().equals("FINDING_DRIVER")){
            log.info("Finding driver for order: " + orderStatusUpdateRequest.getOrderId());
            findDriver(orderStatusUpdateRequest.getOrderId());
        }

        if (orderStatusUpdateRequest.getAction().equals("RESTAURANT_PREPARED_ORDER")){
            order.setOrderStatus("PREPARED");

            orderRepository.save(order);

            log.info("Order is prepared");
            messagingTemplate.convertAndSend("/queue/driver/order/" + order.getDriver().getId(),
                    WebsocketResponse.builder()
                            .action("RESTAURANT_PREPARED_ORDER")
                            .body(
                                    ReceiveOrderDriverResponse.builder()
                                            .orderId(order.getId())
                                            .build()
                            )
                            .build());
        }

        if (orderStatusUpdateRequest.getAction().equals("DRIVER_DELIVERING_ORDER")){
            order.setOrderStatus("DELIVERING");

            orderRepository.save(order);

            log.info("Order is delivering");
            messagingTemplate.convertAndSend("/queue/customer/order/" + order.getUser().getId(),
                    WebsocketResponse.builder()
                            .action("DRIVER_DELIVERING_ORDER")
                            .body(
                                    ReceiveOrderRestaurantResponse.builder()
                                            .orderId(order.getId())
                                            .build()
                            )
                            .build());
        }

        if (orderStatusUpdateRequest.getAction().equals("DRIVER_DELIVERED_ORDER")){
            order.setOrderStatus("DELIVERED");

            orderRepository.save(order);
            log.info("Order is delivered");

            Restaurant restaurant = order.getRestaurant();
            restaurant.setBalance(restaurant.getBalance() + order.getTotalPrice());
            restaurantRepository.save(restaurant);

            Driver driver = order.getDriver();

            if (order.getPaymentMethod().equals("Credit")){
                driver.setBalance(driver.getBalance() + order.getShippingFee());

            } else {
                driver.setBalance(driver.getBalance() - order.getTotalPrice() - order.getShippingFee());
            }

            driverRepository.save(driver);

            messagingTemplate.convertAndSend("/queue/customer/order/" + order.getUser().getId(),
                    WebsocketResponse.builder()
                            .action("DRIVER_DELIVERED_ORDER")
                            .body(
                                    ReceiveOrderRestaurantResponse.builder()
                                            .orderId(order.getId())
                                            .build()
                            )
                            .build());
            messagingTemplate.convertAndSend("/queue/restaurant/order/" + order.getRestaurant().getId(),
                    WebsocketResponse.builder().action("DRIVER_DELIVERED_ORDER").body(
                            ReceiveOrderRestaurantResponse.builder()
                                    .orderId(order.getId())
                                    .build()
                    ).build());
        }


    }

    public void findDriver(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        Driver closestDriver = findClosestDriver(order.getId(), order.getStartLocation());


        if (closestDriver != null) {
            log.info("Sending order to driver: " + closestDriver.getId());
            if (order.getOrderType().equals("FOOD")) {
                messagingTemplate.convertAndSend("/queue/driver/order/" + closestDriver.getId(),
                        WebsocketResponse.builder()
                                .action("RESTAURANT_REQUEST_DRIVER")
                                .body(
                                        ReceiveOrderDriverResponse.builder()
                                                .orderId(orderId)
                                                .orderType(order.getOrderType())
                                                .startLocation(order.getStartLocation())
                                                .endLocation(order.getEndLocation())
                                                .shippingFee(order.getShippingFee())
                                                .build()
                                )
                                .build());
            } else if (order.getOrderType().equals("RIDE")) {
                messagingTemplate.convertAndSend("/queue/driver/order/" + closestDriver.getId(),
                        WebsocketResponse.builder()
                                .action("USER_REQUEST_DRIVER")
                                .body(
                                        ReceiveOrderDriverResponse.builder()
                                                .orderId(orderId)
                                                .orderType(order.getOrderType())
                                                .startLocation(order.getStartLocation())
                                                .endLocation(order.getEndLocation())
                                                .shippingFee(order.getShippingFee())
                                                .build()
                                )
                                .build());
            }

        }

        if (closestDriver == null) {
            log.info("No driver found for order: " + orderId);
            if (order.getOrderType().equals("FOOD")){
                messagingTemplate.convertAndSend("/queue/restaurant/order/" + order.getRestaurant().getId(),
                        WebsocketResponse.builder().action("NO_DRIVER_FOUND").body(
                        ReceiveOrderRestaurantResponse.builder()
                                .orderId(order.getId())
                                .build()
                ).build());

                messagingTemplate.convertAndSend("/queue/customer/order/" + order.getUser().getId(),
                        WebsocketResponse.builder().action("NO_DRIVER_FOUND").body(
                                ReceiveOrderRestaurantResponse.builder()
                                        .orderId(order.getId())
                                        .build()
                        ).build());
                cancelOrder(orderId);

               order.setOrderStatus("CANCELED");
                orderRepository.save(order);

            } else if (order.getOrderType().equals("RIDE")){
                messagingTemplate.convertAndSend("/queue/customer/order/" + order.getUser().getId(),
                        WebsocketResponse.builder().action("NO_DRIVER_FOUND").body(
                        ReceiveOrderRestaurantResponse.builder()
                                .orderId(order.getId())
                                .build()
                ).build());
                cancelOrder(orderId);
                order.setOrderStatus("CANCELED");
                orderRepository.save(order);
            }

        }
    }

    private Driver findClosestDriver(String orderId, Location location) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        List<Driver> drives = driverRepository.findAll();

        for (Driver driver : drives) {
            if (driver.getStatus().equals("ONLINE")) {
                // find closest driver to the restaurant about 1 kilometers
                if (calculateDistance(driver.getLocation().getLatitude(), driver.getLocation().getLongitude(), location.getLatitude(), location.getLongitude()) <= 1) {
                    if (!isDriverInBlacklist(orderId, driver.getId())) {
                        if (order.getPaymentMethod().equals("Credit")){
                            return driver;
                        } else {
                            if (driver.getBalance() >= order.getTotalPrice() + order.getShippingFee()) {
                                return driver;
                            }
                        }
                    }
                }
//                if (order.getPaymentMethod().equals("Credit")){
//                    if (!isDriverInBlacklist(orderId, driver.getId())) {
//                        return driver;
//                    }
//                } else {
//                    if (driver.getBalance() >= order.getTotalPrice() + order.getShippingFee()) {
//                        if (!isDriverInBlacklist(orderId, driver.getId())) {
//                            return driver;
//                        }
//                    }
//                }
            }
        }
        return null;
    }

    public boolean isDriverInBlacklist(String orderId, String driverId) {
        String blacklistKey = "order:blacklist:" + orderId;
        return redisTemplate.opsForSet().isMember(blacklistKey, driverId);
    }

    private String generateOrderCode(String orderType) {
        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        String formattedNumber = String.format("%05d", randomNumber);

        if ("FOOD".equalsIgnoreCase(orderType)) {
            return "F" + formattedNumber;
        } else if ("RIDE".equalsIgnoreCase(orderType)) {
            return "R" + formattedNumber;
        } else {
            throw new IllegalArgumentException("Invalid order type. Use 'FOOD' or 'RIDE'.");
        }
    }

    private void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getPaymentMethod().equals("Credit")){
            stripeService.refundPayment(order.getPaymentIntentId(), order.getTotalPrice() + order.getShippingFee());
        }

        order.setOrderStatus("CANCELED");
        orderRepository.save(order);
    }
}
