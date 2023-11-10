package com.api.fakestore.repositories;

import com.api.fakestore.models.CategoryModel;
import com.api.fakestore.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<ProductModel, Integer> {
    List<ProductModel> findByCategory(CategoryModel categoryModel);
}
