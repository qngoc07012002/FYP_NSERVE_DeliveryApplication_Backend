package nserve.delivery_application_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.request.Food.FoodCreationRequest;
import nserve.delivery_application_backend.dto.request.Food.FoodStatusUpdateRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class FoodService {

    private static final String IMAGE_DIR = "path/to/your/image/directory/";

    FoodRepository foodRepository;
    CategoryRepository categoryRepository;
    RestaurantRepository restaurantRepository;


    FoodMapper foodMapper;

    public List<FoodResponse> getAllFoods() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Restaurant restaurant = restaurantRepository.findByOwnerId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        List<Food> foods = foodRepository.findByRestaurant(restaurant);

        return foods.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    public FoodResponse getFoodById(String id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));
        return convertToResponse(food);
    }

    public FoodResponse createFood(FoodCreationRequest foodRequest)  {

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Restaurant restaurant = restaurantRepository.findByOwnerId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        Food food = new Food();
        food.setName(foodRequest.getName());
        food.setDescription(foodRequest.getDescription());
        food.setPrice(foodRequest.getPrice());
        food.setRestaurant(restaurant);
        food.setImgUrl(foodRequest.getImgUrl());
        food.setStatus("ACTIVE");
        food.setCreateAt(new Date());
        food.setUpdateAt(new Date());

        foodRepository.save(food);
        return foodMapper.toFoodResponse(food);
    }

    public FoodResponse updateFood(String foodId, FoodUpdateRequest request) throws IOException {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Restaurant restaurant = restaurantRepository.findByOwnerId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (!food.getRestaurant().getId().equals(restaurant.getId())) {
            throw new AppException(ErrorCode.FOOD_NOT_FOUND);
        }



        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        if (request.getImgUrl() != null ) {
            food.setImgUrl(request.getImgUrl());
        }

        Food updatedFood = foodRepository.save(food);
        return foodMapper.toFoodResponse(updatedFood);
    }

    public void deleteFood(String id) {

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Restaurant restaurant = restaurantRepository.findByOwnerId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        if (!food.getRestaurant().getId().equals(restaurant.getId())) {
            throw new AppException(ErrorCode.FOOD_NOT_FOUND);
        }

        
        foodRepository.deleteById(id);
    }


    private FoodResponse convertToResponse(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imgUrl(food.getImgUrl())
                .status(food.getStatus())
                .build();
    }

    public FoodResponse updateFoodStatus(String foodId, FoodStatusUpdateRequest statusUpdateRequest) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        food.setStatus(statusUpdateRequest.getStatus());

        return foodMapper.toFoodResponse(foodRepository.save(food));
    }

    public List<FoodResponse> getAllFoodsByRestaurantId(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        List<Food> foods = foodRepository.findByRestaurant(restaurant);

        foods = foods.stream().filter(food -> food.getStatus().equals("ACTIVE")).toList();

        return foods.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
}
