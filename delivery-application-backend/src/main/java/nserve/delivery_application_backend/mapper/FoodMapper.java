package nserve.delivery_application_backend.mapper;


import nserve.delivery_application_backend.dto.request.Food.FoodCreationRequest;
import nserve.delivery_application_backend.dto.response.FoodResponse;
import nserve.delivery_application_backend.entity.Food;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodMapper {
    Food toFood(FoodCreationRequest request);

    FoodResponse toFoodResponse(Food food);

    List<FoodResponse> toFoodResponses(List<Food> foods);
}