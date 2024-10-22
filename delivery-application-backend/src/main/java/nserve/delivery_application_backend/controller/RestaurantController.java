package nserve.delivery_application_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantCreationRequest;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantUpdateRequest;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.RestaurantResponse;
import nserve.delivery_application_backend.entity.Restaurant;
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

    @GetMapping()
    List<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/{restaurantId}")
    RestaurantResponse getRestaurant(@PathVariable("restaurantId") String restaurantId) {
        return restaurantService.getRestaurant(restaurantId);
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
}
