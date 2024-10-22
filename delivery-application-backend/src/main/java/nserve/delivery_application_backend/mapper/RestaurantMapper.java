package nserve.delivery_application_backend.mapper;

import nserve.delivery_application_backend.dto.request.Restaurant.RestaurantCreationRequest;
import nserve.delivery_application_backend.dto.response.RestaurantResponse;
import nserve.delivery_application_backend.entity.Restaurant;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    Restaurant toRestaurant(RestaurantCreationRequest request);

    RestaurantResponse toRestaurantResponse(Restaurant restaurant);

    List<RestaurantResponse> toRestaurantResponses(List<Restaurant> restaurants);
}
