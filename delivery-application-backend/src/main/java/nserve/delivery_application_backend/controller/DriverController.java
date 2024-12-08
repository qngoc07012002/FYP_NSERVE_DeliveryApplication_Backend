package nserve.delivery_application_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.Driver.DriverLocationUpdateRequest;
import nserve.delivery_application_backend.dto.request.Driver.DriverStatusUpdateRequest;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantStatusUpdateRequest;
import nserve.delivery_application_backend.dto.request.UserCreationRequest;
import nserve.delivery_application_backend.dto.request.UserUpdateRequest;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.DriverResponse;
import nserve.delivery_application_backend.dto.response.Restaurant.RestaurantResponse;
import nserve.delivery_application_backend.dto.response.UserResponse;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.service.DriverService;
import nserve.delivery_application_backend.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class DriverController {
    DriverService driverService;

    @GetMapping("/info")
    ApiResponse<DriverResponse> getDriver() {
        return ApiResponse.<DriverResponse>builder()
                .code(1000)
                .message("Driver info retrieved successfully")
                .result(driverService.getDriver())
                .build();
    }

    @PutMapping("/status/{status}")
    public ApiResponse<DriverResponse> updateDriverStatus(
            @PathVariable("status") String status) {
        DriverResponse driverResponse = driverService.updateDriverStatus(status);
        return ApiResponse.<DriverResponse>builder()
                .code(1000)
                .message("Driver status updated successfully")
                .result(driverResponse)
                .build();
    }

    @PutMapping("/location")
    public ApiResponse<DriverResponse> updateDriverLocation(
            @Valid @RequestBody DriverLocationUpdateRequest driverLocationUpdateRequest) {
        driverService.updateDriverLocation(driverLocationUpdateRequest);
        return ApiResponse.<DriverResponse>builder()
                .code(1000)
                .message("Driver location updated successfully")
                .build();
    }
}
