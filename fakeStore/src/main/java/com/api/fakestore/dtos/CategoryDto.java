package com.api.fakestore.dtos;

import jakarta.validation.constraints.NotBlank;

public class CategoryDto {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
