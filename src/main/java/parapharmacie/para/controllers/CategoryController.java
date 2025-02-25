package parapharmacie.para.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import parapharmacie.para.entities.Category;
import parapharmacie.para.services.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    public final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération des catégories.");
        }
    }
}
