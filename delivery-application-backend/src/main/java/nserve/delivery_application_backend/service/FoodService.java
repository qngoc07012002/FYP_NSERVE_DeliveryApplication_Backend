package nserve.delivery_application_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.entity.Food;
import nserve.delivery_application_backend.repository.FoodRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class FoodService {

    private FoodRepository foodRepository;

    private final String imageDirectory = "src/main/resources/static/images/";

    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public Optional<Food> getFoodById(String id) {
        return foodRepository.findById(id);
    }

    public Food createFood(Food food, MultipartFile image) throws IOException {
        String imageUrl = saveImage(image);
        food.setImgUrl(imageUrl);
        return foodRepository.save(food);
    }

    public Food updateFood(String id, Food foodDetails, MultipartFile image) throws IOException {
        Food food = foodRepository.findById(id).orElseThrow(() -> new RuntimeException("Food not found"));

        food.setName(foodDetails.getName());
        food.setDescription(foodDetails.getDescription());
        food.setPrice(foodDetails.getPrice());
        food.setCategory(foodDetails.getCategory());
        food.setRestaurant(foodDetails.getRestaurant());

        if (!image.isEmpty()) {
            String imageUrl = saveImage(image);
            food.setImgUrl(imageUrl);
        }

        return foodRepository.save(food);
    }


    public void deleteFood(String id) {
        foodRepository.deleteById(id);
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            File file = new File(imageDirectory + fileName);
            image.transferTo(file);
            return "/images/" + fileName;
        }
        return null;
    }
}
