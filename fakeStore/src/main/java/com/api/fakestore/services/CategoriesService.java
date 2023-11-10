package com.api.fakestore.services;

import com.api.fakestore.dtos.CategoryDto;
import com.api.fakestore.exceptions.ExceptionCustom;
import com.api.fakestore.models.CategoryModel;
import com.api.fakestore.outputs.ProductOutput;
import com.api.fakestore.repositories.CategoriesRepository;
import com.api.fakestore.repositories.ProductsRepository;
import com.api.fakestore.utils.ProductUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final ProductsRepository productsRepository;

    public CategoriesService(CategoriesRepository categoriesRepository, ProductsRepository productsRepository) {
        this.categoriesRepository = categoriesRepository;
        this.productsRepository = productsRepository;
    }

    public List<String> findAll() {
        var categories = categoriesRepository.findAll();
        return getCategoriesNames(categories);
    }

    public List<ProductOutput> findProductsByCategory(String categoryName) {
        CategoryModel category = getAndVerifyCategoryByName(categoryName);
        return ProductUtils.makeProductOutputList(productsRepository.findByCategory(category));
    }

    public CategoryModel save(CategoryDto categoryDto) {
        var categoryModel = new CategoryModel();
        BeanUtils.copyProperties(categoryDto, categoryModel);
        return categoriesRepository.save(categoryModel);
    }

    public CategoryModel update(String categoryName, CategoryDto categoryDto) {
        CategoryModel category = getAndVerifyCategoryByName(categoryName);

        var categoryUpdate = new CategoryModel();
        categoryUpdate.setId(category.getId());
        categoryUpdate.setName(categoryDto.getName());

        return categoriesRepository.save(categoryUpdate);
    }

    public void delete(String categoryName) {
        CategoryModel category = getAndVerifyCategoryByName(categoryName);
        categoriesRepository.delete(category);
    }

    public List<String> getCategoriesNames (List<CategoryModel> categoryModels) {
        List<String> categories = new ArrayList<>();
        for (CategoryModel category: categoryModels) {
            categories.add(category.getName());
        }
        return categories;
    }

    public CategoryModel getAndVerifyCategoryByName(String categoryName) {
        Optional<CategoryModel> categoryModel = categoriesRepository.findByName(categoryName);
        if(categoryModel.isEmpty()) throw new ExceptionCustom("Categoria não encontrada!", HttpStatus.NOT_FOUND);

        return categoryModel.get();
    }

}
