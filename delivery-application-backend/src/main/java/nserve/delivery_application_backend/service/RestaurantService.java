package nserve.delivery_application_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantStatusUpdateRequest;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantUpdateRequest;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantCreationRequest;
import nserve.delivery_application_backend.dto.response.Restaurant.RestaurantResponse;
import nserve.delivery_application_backend.entity.Restaurant;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import nserve.delivery_application_backend.mapper.RestaurantMapper; // Giả sử bạn có một mapper cho Restaurant
import nserve.delivery_application_backend.repository.RestaurantRepository;
import nserve.delivery_application_backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class RestaurantService {
    RestaurantRepository restaurantRepository;
    RestaurantMapper restaurantMapper;
    UserRepository userRepository;
    public RestaurantResponse createRestaurant(RestaurantCreationRequest request) {
        Restaurant restaurant = restaurantMapper.toRestaurant(request);
        User user = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        restaurant.setOwner(user);

        RestaurantResponse restaurantResponse = restaurantMapper.toRestaurantResponse(restaurantRepository.save(restaurant));


        return restaurantResponse;
    }

   // @PreAuthorize("hasRole('CUSTOMER')")
    public List<RestaurantResponse> getAllRestaurants() {


        return restaurantMapper.toRestaurantResponses(restaurantRepository.findAll());
    }

    public RestaurantResponse getRestaurant() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Restaurant restaurant = restaurantRepository.findByOwnerId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));
        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    public RestaurantResponse updateRestaurant(String restaurantId, RestaurantUpdateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.setRestaurantName(request.getRestaurantName());
        restaurant.setAddress(request.getAddress());
        restaurant.setRating(request.getRating());

        return restaurantMapper.toRestaurantResponse(restaurantRepository.save(restaurant));
    }

    public void deleteRestaurant(String restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new AppException(ErrorCode.RESTAURANT_NOT_FOUND);
        }
        restaurantRepository.deleteById(restaurantId);
    }

    public RestaurantResponse updateRestaurantStatus(String restaurantId, RestaurantStatusUpdateRequest statusUpdateRequest) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        log.info("userId: {}", userId);
        log.info("restaurant owner id: {}", restaurant.getOwner().getId());
        if (!userId.equals(restaurant.getOwner().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        restaurant.setStatus(statusUpdateRequest.getStatus());

        return restaurantMapper.toRestaurantResponse(restaurantRepository.save(restaurant));
    }
}
