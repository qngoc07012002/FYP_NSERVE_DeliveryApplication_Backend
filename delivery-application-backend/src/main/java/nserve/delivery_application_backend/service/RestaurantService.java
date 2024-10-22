package nserve.delivery_application_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantUpdateRequest;
import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantCreationRequest;
import nserve.delivery_application_backend.dto.response.RestaurantResponse;
import nserve.delivery_application_backend.entity.Restaurant;
import nserve.delivery_application_backend.entity.User;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import nserve.delivery_application_backend.mapper.RestaurantMapper; // Giả sử bạn có một mapper cho Restaurant
import nserve.delivery_application_backend.repository.RestaurantRepository;
import nserve.delivery_application_backend.repository.UserRepository;
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
        restaurantResponse.setOwner(restaurant.getOwner());

        return restaurantResponse;
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantMapper.toRestaurantResponses(restaurantRepository.findAll());
    }

    public RestaurantResponse getRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
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
}
