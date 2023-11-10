package com.api.fakestore.controllers;

import com.api.fakestore.dtos.CategoryDto;
import com.api.fakestore.services.CategoriesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping("/categories")
    public ResponseEntity<Object> indexCategories() {
        return ResponseEntity.ok().body(categoriesService.findAll());
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Object> indexCategories(@PathVariable(value = "categoryName")String categoryName) {
        return ResponseEntity.ok().body(categoriesService.findProductsByCategory(categoryName));
    }

    @PostMapping("/categories")
    public ResponseEntity<Object> insertCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.OK).body(categoriesService.save(categoryDto));
    }

    @PatchMapping("/category/{categoryName}")
    public ResponseEntity<Object> updateCategory(@PathVariable(value = "categoryName") String categoryName,
                                                 @RequestBody @Valid CategoryDto categoryDto) {
        return ResponseEntity.ok().body(categoriesService.update(categoryName, categoryDto));
    }

    @DeleteMapping("/category/{categoryName}")
    public ResponseEntity<Object> deleteCategory(@PathVariable(value = "categoryName")String categoryName) {
        categoriesService.delete(categoryName);
        return ResponseEntity.ok().body("Categoria deletada com sucesso!");
    }
}
