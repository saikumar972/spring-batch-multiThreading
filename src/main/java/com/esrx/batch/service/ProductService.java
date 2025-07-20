package com.esrx.batch.service;

import com.esrx.batch.entity.Product;
import com.esrx.batch.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductRepo repo;
    public List<Product> saveAllProducts(List<Product> productList){
        return repo.saveAll(productList);
    }

    public List<Product> resetProducts() {
        List<Product> productList=repo.findAll();
        List<Product> resetProducts= productList.stream().map(this::resetProduct).toList();
        repo.saveAll(resetProducts);
        return resetProducts;
    }
    public  Product resetProduct(Product product){
        product.setDiscount(0);
        product.setEffectivePrice(product.getPrice());
        product.setIsDiscountApplicable(0);
        return product;
    }

    public void processProductIds(List<Long> batch) {
        batch.parallelStream().forEach(this::updateProductDetails);
    }

    public void processProductIdsParallelStream(List<Long> batch) {
        batch.parallelStream().forEach(this::updateProductDetails);
    }

    public void updateProductDetails(Long productId){
        Product updatedProducts=repo.findById(productId).orElseThrow(()->new IllegalArgumentException("Invalid id"));
        updateProduct(updatedProducts);
        repo.save(updatedProducts);
    }

    public void updateProduct(Product product){
        product.setDiscount(product.getPrice()>300?10:20);
        product.setEffectivePrice((int)(product.getPrice())*(100- product.getDiscount())/100);
        product.setIsDiscountApplicable(1);
    }
}
