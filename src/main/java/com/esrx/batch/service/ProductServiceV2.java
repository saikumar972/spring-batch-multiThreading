package com.esrx.batch.service;

import com.esrx.batch.entity.Product;
import com.esrx.batch.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceV2 {
    @Autowired
    ProductRepo repo;

    public void updateProductDetailsUsingBatches(List<Long> productsIds){
        List<List<Long>> productIdIntoBatches=splitIntoBatches(productsIds,200);
        List<CompletableFuture<Void>> futures = productIdIntoBatches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> {
                    processProductIds(batch);
                })).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public List<List<Long>> splitIntoBatches(List<Long> productIds,int batchSize){
        List<List<Long>> productBatches=new ArrayList<>();
        int totalSize=productIds.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            int end = Math.min(i + batchSize, totalSize);
            productBatches.add(productIds.subList(i, end));
        }
        return productBatches;
    }

    private void processProductIds(List<Long> batch) {
        System.out.println("Processing batch " + batch + " by thread " + Thread.currentThread().getName());
        batch.forEach(this::updateProductDetails);
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
