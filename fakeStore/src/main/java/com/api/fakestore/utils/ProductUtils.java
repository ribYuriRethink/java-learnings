package com.api.fakestore.utils;

import com.api.fakestore.models.ProductModel;
import com.api.fakestore.outputs.ProductOutput;
import com.api.fakestore.outputs.ProductRating;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductUtils {
    public static ProductOutput makeProductOutput(ProductModel productModel){
        var productOutput = new ProductOutput();
        BeanUtils.copyProperties(productModel, productOutput);
        var productRating = new ProductRating(productModel.getRate(), productModel.getCount());
        productOutput.setCategory(productModel.getCategory().getName());
        productOutput.setRating(productRating);
        return productOutput;
    }

    public static List<ProductOutput> makeProductOutputList (List<ProductModel> productModels){
        List<ProductOutput> productOutputs = new ArrayList<>();
        for (ProductModel product: productModels) {
            productOutputs.add(makeProductOutput(product));
        }
        return productOutputs;
    }
}
