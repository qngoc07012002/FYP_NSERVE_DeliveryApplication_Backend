package nserve.delivery_application_backend.dto.response.Order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.*;
import nserve.delivery_application_backend.entity.Location;

import java.util.Date;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDriverResponse {
    private String id;
    private String orderCode;
    private String orderType;
    private RestaurantInfo restaurantInfo;
    private UserInfo userInfo;
    private Location startLocation;
    private Location endLocation;
    private String orderStatus;
    private float shippingFee;
    private Date createdAt;

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
    public static class UserInfo {
        private String name;
        private String img;
    }
}


