package com.api.fakestore.services;

import com.api.fakestore.dtos.ProductDto;
import com.api.fakestore.exceptions.ExceptionCustom;
import com.api.fakestore.models.CategoryModel;
import com.api.fakestore.models.ProductModel;
import com.api.fakestore.outputs.ProductOutput;
import com.api.fakestore.repositories.CategoriesRepository;
import com.api.fakestore.repositories.ProductsRepository;
import com.api.fakestore.utils.ProductUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsServices {

    private final ProductsRepository productsRepository;
    private final CategoriesRepository categoriesRepository;


    public ProductsServices(ProductsRepository productsRepository, CategoriesRepository categoriesRepository) {
        this.productsRepository = productsRepository;
        this.categoriesRepository = categoriesRepository;
    }


    public List<ProductOutput> findAll() {
        return ProductUtils.makeProductOutputList(productsRepository.findAll());
    }

    public ProductOutput findById(Integer id) {
        return ProductUtils.makeProductOutput(getAndVerifyProduct(id));
    }

    public ProductOutput save(ProductDto productDto) {
        CategoryModel category = getAndVerifyCategory(productDto.getCategory_id());

        var productModel = new ProductModel();
        BeanUtils.copyProperties(productDto, productModel);
        productModel.setCategory(category);

        return ProductUtils.makeProductOutput(productsRepository.save(productModel));
    }

    public ProductOutput update(Integer id,ProductDto productDto) {
        ProductModel product = getAndVerifyProduct(id);
        CategoryModel category = getAndVerifyCategory(productDto.getCategory_id());

        var productUpdate = new ProductModel();
        BeanUtils.copyProperties(productDto, productUpdate);
        productUpdate.setCategory(category);
        productUpdate.setId(product.getId());

        return ProductUtils.makeProductOutput(productsRepository.save(productUpdate));
    }

    public void delete(Integer id) {
        ProductModel product = getAndVerifyProduct(id);
        productsRepository.delete(product);
    }

    public CategoryModel getAndVerifyCategory (Integer categoryId) throws ExceptionCustom {
        Optional<CategoryModel> category = categoriesRepository.findById(categoryId);
        if (category.isEmpty()) throw new ExceptionCustom("Categoria não encontrada!", HttpStatus.NOT_FOUND);

        return category.get();
    }

    public ProductModel getAndVerifyProduct (Integer productID) {
        Optional<ProductModel> productModel = productsRepository.findById(productID);
        if (productModel.isEmpty())
            throw new ExceptionCustom("Produto não encontrado!", HttpStatus.NOT_FOUND);

        return productModel.get();
    }
}
