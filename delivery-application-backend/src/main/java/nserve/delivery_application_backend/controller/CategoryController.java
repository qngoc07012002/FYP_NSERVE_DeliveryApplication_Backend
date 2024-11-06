package nserve.delivery_application_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.response.ApiResponse;
import nserve.delivery_application_backend.entity.Category;
import nserve.delivery_application_backend.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class CategoryController {
    CategoryService categoryService;

    @PostMapping()
    ApiResponse<Category> createCategory(@RequestBody @Valid Category category) {
        ApiResponse<Category> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.createCategory(category));
        return apiResponse;
    }

    @GetMapping()
    ApiResponse<List<Category>> getAllCategories() {
        ApiResponse<List<Category>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.getAllCategories());
        return apiResponse;
    }

    @GetMapping("/{categoryId}")
    Category getCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.getCategory(categoryId);
    }

    @PutMapping("/{categoryId}")
    Category updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody Category category) {
        return categoryService.updateCategory(categoryId, category);
    }

    @DeleteMapping("/{categoryId}")
    String deleteCategory(@PathVariable("categoryId") String categoryId) {
        categoryService.deleteCategory(categoryId);
        return "Category deleted";
    }
}
