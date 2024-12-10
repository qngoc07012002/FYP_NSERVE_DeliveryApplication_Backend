package nserve.delivery_application_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantCreationRequest;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantStatusUpdateRequest;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantUpdateRequest;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.Restaurant.RestaurantResponse;
import nserve.delivery_application_backend.service.RestaurantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class RestaurantController {
    RestaurantService restaurantService;

    @PostMapping()
    ApiResponse<RestaurantResponse> createRestaurant(@RequestBody @Valid RestaurantCreationRequest request) {
        ApiResponse<RestaurantResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(restaurantService.createRestaurant(request));
        return apiResponse;
    }

    @GetMapping("/checkRestaurantStatus/{restaurantId}")
    public ApiResponse<String> checkRestaurantStatus(@PathVariable String restaurantId) {
        try {
            String status = restaurantService.checkRestaurantStatus(restaurantId);
            return ApiResponse.<String>builder()
                    .result(status)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .result("Unable to fetch restaurant status: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping()
    List<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/info")
    RestaurantResponse getRestaurant() {
        return restaurantService.getRestaurant();
    }

    @PutMapping("/{restaurantId}")
    RestaurantResponse updateRestaurant(@PathVariable("restaurantId") String restaurantId, @RequestBody RestaurantUpdateRequest request) {
        return restaurantService.updateRestaurant(restaurantId, request);
    }

    @DeleteMapping("/{restaurantId}")
    String deleteRestaurant(@PathVariable("restaurantId") String restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return "Restaurant deleted";
    }

    @PutMapping("/{restaurantId}/status")
    public ApiResponse<RestaurantResponse> updateRestaurantStatus(
            @PathVariable("restaurantId") String restaurantId,
            @RequestBody RestaurantStatusUpdateRequest statusUpdateRequest) {
        RestaurantResponse restaurantResponse = restaurantService.updateRestaurantStatus(restaurantId, statusUpdateRequest);
        return ApiResponse.<RestaurantResponse>builder()
                .code(1000)
                .message("Restaurant status updated successfully")
                .result(restaurantResponse)
                .build();
    }
}
