package com.esrx.batch.controller;

import com.esrx.batch.entity.Product;
import com.esrx.batch.service.ProductService;
import com.esrx.batch.service.ProductServiceV2;
import com.esrx.batch.service.ProductServiceV3;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {
    ProductService productService;
    ProductServiceV2 productServiceV2;
    ProductServiceV3 productServiceV3;
    //To do while saving also multi threading
    @PostMapping("/saveAll")
    public List<Product> productList(@RequestBody List<Product> productList){
        return productService.saveAllProducts(productList);
    }

    @PostMapping("/saveAllV2")
    public String saveProductsV2(@RequestBody List<Product> productList){
        productServiceV3.saveAllProducts(productList);
         return "success";
    }

    @PostMapping("/reset")
    public List<Product> reset(){
        return productService.resetProducts();
    }

    @PostMapping("/update")
    public String update(@RequestBody List<Long> productIds){
         productService.processProductIds(productIds);
         return "success";
    }

    @PostMapping("/updateParallel")
    public String updateParallel(@RequestBody List<Long> productIds){
        productService.processProductIdsParallelStream(productIds);
        return "success";
    }

    @PostMapping("/updateV2")
    public String multiThreading(@RequestBody List<Long> productIds){
        productServiceV2.updateProductDetailsUsingBatches(productIds);
        return "Success";
    }
}
