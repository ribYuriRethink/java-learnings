package com.api.fakestore.repositories;

import com.api.fakestore.models.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<CategoryModel, Integer> {
    Optional<CategoryModel> findByName(String name);
}
