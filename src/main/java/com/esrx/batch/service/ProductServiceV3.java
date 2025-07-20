package com.esrx.batch.service;

import com.esrx.batch.entity.Product;
import com.esrx.batch.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceV3 {
    @Autowired
    ProductRepo repo;
    public void saveAllProducts(List<Product> productList){
        List<List<Product>> productBatches=productBatches(productList,200);
        List<CompletableFuture<Void>> futures = productBatches.stream()
                .map(productbatch -> CompletableFuture.runAsync(() -> {
                    saveProducts(productbatch);
                })).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void saveProducts(List<Product> productList){
        System.out.println("Processing batch " + productList + " by thread " + Thread.currentThread().getName());
        repo.saveAll(productList);
    }

    public List<List<Product>> productBatches(List<Product> productList,int batchSize){
        List<List<Product>> productBatches=new ArrayList<>();
        int totalSize=productList.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            int end = Math.min(i + batchSize, totalSize);
            productBatches.add(productList.subList(i, end));
        }
        return productBatches;
    }

}
