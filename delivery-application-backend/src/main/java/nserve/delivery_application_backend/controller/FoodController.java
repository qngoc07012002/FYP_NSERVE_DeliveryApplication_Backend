package nserve.delivery_application_backend.controller;

import nserve.delivery_application_backend.entity.Food;
import nserve.delivery_application_backend.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping
    public List<Food> getAllFoods() {
        return foodService.getAllFoods();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable String id) {
        return foodService.getFoodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Food> createFood(
            @ModelAttribute Food food,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        Food createdFood = foodService.createFood(food, image);
        return ResponseEntity.ok(createdFood);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(
            @PathVariable String id,
            @ModelAttribute Food food,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        Food updatedFood = foodService.updateFood(id, food, image);
        return ResponseEntity.ok(updatedFood);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable String id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}
