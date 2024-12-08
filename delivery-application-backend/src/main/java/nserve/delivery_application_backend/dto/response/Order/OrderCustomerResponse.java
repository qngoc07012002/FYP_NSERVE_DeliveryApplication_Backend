package nserve.delivery_application_backend.dto.response.Order;

import lombok.*;
import nserve.delivery_application_backend.entity.Location;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCustomerResponse {
    private String id;
    private String orderCode;
    private String orderType;
    private RestaurantInfo restaurantInfo;
    private DriverInfo driverInfo;
    private Location startLocation;
    private Location endLocation;
    private String orderStatus;
    private float shippingFee;
    private float totalPrice;
    private Date createdAt;
    List<OrderItemResponse> orderItems;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantInfo {
        private String name;
        private String img;
        private String restaurantLocation;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverInfo {
        private String name;
        private String img;
        private String phoneNumber;
    }
}
