package nserve.delivery_application_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import nserve.delivery_application_backend.dto.request.Food.FoodCreationRequest;
import nserve.delivery_application_backend.dto.request.Food.FoodStatusUpdateRequest;
import nserve.delivery_application_backend.dto.request.Food.FoodUpdateRequest;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.dto.response.FoodResponse;
import nserve.delivery_application_backend.entity.Food;
import nserve.delivery_application_backend.service.FoodService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class FoodController {
    FoodService foodService;

    @PostMapping()
    public ApiResponse<FoodResponse> createFood(
            @RequestBody FoodCreationRequest foodRequest) {


        try {
            FoodResponse foodResponse = foodService.createFood(foodRequest);
            return ApiResponse.<FoodResponse>builder()
                    .code(1000)
                    .message("Food created successfully")
                    .result(foodResponse)
                    .build();
        } catch (IOException e) {
            return ApiResponse.<FoodResponse>builder()
                    .code(5000)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    @PutMapping("/{foodId}")
    public ApiResponse<FoodResponse> updateFood(
            @RequestBody FoodUpdateRequest foodRequest) {
        try {
            FoodResponse foodResponse = foodService.updateFood(foodRequest);
            return ApiResponse.<FoodResponse>builder()
                    .code(1000)
                    .message("Food updated successfully")
                    .result(foodResponse)
                    .build();
        } catch (IOException e) {
            return ApiResponse.<FoodResponse>builder()
                    .code(5000)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping()
    public ApiResponse<List<FoodResponse>> getAllFoods() {
        List<FoodResponse> foodResponses = foodService.getAllFoods();
        return ApiResponse.<List<FoodResponse>>builder()
                .code(1000)
                .message("Foods retrieved successfully")
                .result(foodResponses)
                .build();
    }

    @GetMapping("/{foodId}")
    public ApiResponse<FoodResponse> getFoodById(@PathVariable("foodId") String foodId) {
        FoodResponse foodResponse = foodService.getFoodById(foodId);
        return ApiResponse.<FoodResponse>builder()
                .code(1000)
                .message("Food retrieved successfully")
                .result(foodResponse)
                .build();
    }

    @DeleteMapping("/{foodId}")
    public ApiResponse<String> deleteFood(@PathVariable("foodId") String foodId) {
        foodService.deleteFood(foodId);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Food deleted successfully")
                .result("Food with ID " + foodId + " deleted.")
                .build();
    }

    @PutMapping("/{foodId}/status")
    public ApiResponse<FoodResponse> updateFoodStatus(
            @PathVariable("foodId") String foodId,
            @RequestBody FoodStatusUpdateRequest statusUpdateRequest) {
        FoodResponse foodResponse = foodService.updateFoodStatus(foodId, statusUpdateRequest);
        return ApiResponse.<FoodResponse>builder()
                .code(1000)
                .message("Food status updated successfully")
                .result(foodResponse)
                .build();
    }

}

