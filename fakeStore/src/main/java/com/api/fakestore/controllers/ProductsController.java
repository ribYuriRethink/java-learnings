package com.api.fakestore.controllers;

import com.api.fakestore.dtos.ProductDto;
import com.api.fakestore.services.ProductsServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductsServices productsServices;

    public ProductsController(ProductsServices productsServices) {
        this.productsServices = productsServices;
    }

    @GetMapping
    public ResponseEntity<Object> indexProducts() {
        return ResponseEntity.ok().body(productsServices.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> showProduct(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok().body(productsServices.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> insertProduct(@RequestBody @Valid ProductDto productDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(productsServices.save(productDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") Integer id,
                                                @RequestBody @Valid ProductDto productDto) {
            return ResponseEntity.ok().body(productsServices.update(id, productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id")Integer id) {
        productsServices.delete(id);
        return ResponseEntity.ok().body("Produto deletado com sucesso!");
    }
}
