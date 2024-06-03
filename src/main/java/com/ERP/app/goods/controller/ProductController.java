package com.ERP.app.goods.controller;

import com.ERP.app.goods.data.ArticleWarehouse;
import com.ERP.app.goods.data.Product;
import com.ERP.app.goods.repository.ArticleWarehouseRepository;
import com.ERP.app.goods.repository.ProductRepository;
import com.ERP.app.goods.services.ArticleWarehouseService;
import com.ERP.app.goods.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ArticleWarehouseService articleWarehouseService;

    @Autowired
    private ArticleWarehouseRepository articleWarehouseRepository;

    @GetMapping("/products")
    public List<Product> GetAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping("/products/add")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }


   @PostMapping("/products/addToWarehouse")
    public ResponseEntity<String> updateState(@RequestBody Map<String, Object> requestBody) {
        try {
            int supplierId = (int) requestBody.get("supplierId");
            int warehouseId = (int) requestBody.get("warehouseId");
            int quantity = (int) requestBody.get("quantity");
            List<Map<String, Object>> articleList = (List<Map<String, Object>>) requestBody.get("articles");
            List<ArticleWarehouse> articles = new ArrayList<>();

            for (Map<String, Object> articleMap : articleList) {

                Map<String, Object> productMap = (Map<String, Object>) articleMap.get("product");
                long productId = ((Number) productMap.get("id")).longValue();
                Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchElementException("Product not found"));
                double purchasePrice = ((Number) articleMap.get("purchasePrice")).doubleValue();

                Optional<ArticleWarehouse> articleWarehouse = articleWarehouseRepository.findArticleWarehouse(product, purchasePrice);
                if (articleWarehouse.isEmpty()) {
                    ArticleWarehouse articleWarehouse1 = new ArticleWarehouse(product, purchasePrice);
                    articleWarehouseRepository.save(articleWarehouse1);
                    articles.add(articleWarehouse1);
                } else {
                    articles.add(articleWarehouse.get());
                }
            }

            productService.receptionOfProducts(supplierId, warehouseId, quantity, articles);
            return ResponseEntity.ok("Products successfully added to warehouse");

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding products: " + e.getMessage());
        }
    }

    @GetMapping("/productData/{productId}")
    public ResponseEntity<String> getProductData(@PathVariable("productId") long productId) {
        try{
            return ResponseEntity.ok(productService.getProductData(productId));
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting product data: " + e.getMessage());
        }
    }

    @GetMapping("/productState/{productId}")
    public ResponseEntity<String> getProductState(@PathVariable("productId") long productId) {
        try{
            return ResponseEntity.ok(productService.getProductState(productId));
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting product state: " + e.getMessage());
        }
    }
}
