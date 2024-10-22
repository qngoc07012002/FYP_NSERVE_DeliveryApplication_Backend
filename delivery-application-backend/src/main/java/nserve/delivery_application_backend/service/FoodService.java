package nserve.delivery_application_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import nserve.delivery_application_backend.dto.request.Food.FoodCreationRequest;
import nserve.delivery_application_backend.dto.request.Food.FoodUpdateRequest;
import nserve.delivery_application_backend.dto.response.FoodResponse;
import nserve.delivery_application_backend.entity.Food;
import nserve.delivery_application_backend.entity.Category;
import nserve.delivery_application_backend.entity.Restaurant;
import nserve.delivery_application_backend.exception.AppException;
import nserve.delivery_application_backend.exception.ErrorCode;
import nserve.delivery_application_backend.mapper.FoodMapper;
import nserve.delivery_application_backend.repository.FoodRepository;
import nserve.delivery_application_backend.repository.CategoryRepository;
import nserve.delivery_application_backend.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class FoodService {

    FoodRepository foodRepository;
    CategoryRepository categoryRepository;
    RestaurantRepository restaurantRepository;

    FoodMapper foodMapper;

    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public FoodResponse getFoodById(String id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));
        return convertToResponse(food);
    }

    public FoodResponse createFood(FoodCreationRequest foodRequest) throws IOException {
        Category category = categoryRepository.findById(foodRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Restaurant restaurant = restaurantRepository.findById(foodRequest.getRestaurantId())
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        Food food = new Food();
        food.setName(foodRequest.getName());
        food.setDescription(foodRequest.getDescription());
        food.setPrice(foodRequest.getPrice());
        food.setCategory(category);
        food.setRestaurant(restaurant);
        food.setImgUrl(foodRequest.getImgUrl());

        Food savedFood = foodRepository.save(food);
        return foodMapper.toFoodResponse(food);
    }

    public FoodResponse updateFood(FoodUpdateRequest request) throws IOException {
        Food food = foodRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        food.setCategory(category);
        food.setRestaurant(restaurant);

        if (request.getImgUrl() != null ) {
            food.setImgUrl(request.getImgUrl());
        }

        Food updatedFood = foodRepository.save(food);
        return foodMapper.toFoodResponse(updatedFood);
    }

    public void deleteFood(String id) {
        foodRepository.deleteById(id);
    }


    private FoodResponse convertToResponse(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imgUrl(food.getImgUrl())
                .category(food.getCategory())
                .restaurant(food.getRestaurant())
                .build();
    }
}
