package com.ERP.app.goods.controller;

import com.ERP.app.goods.data.ArticleWarehouse;
import com.ERP.app.goods.services.ArticleWarehouseService;
import com.ERP.app.goods.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/article-warehouse")
public class ArticleWarehouseController {

    @Autowired
    private ArticleWarehouseService articleWarehouseService;


    @PostMapping
    public ResponseEntity<ArticleWarehouse> createArticleWarehouse(@RequestBody ArticleWarehouse articleWarehouse) {
        ArticleWarehouse savedArticleWarehouse = articleWarehouseService.saveArticleWarehouse(articleWarehouse);
        return ResponseEntity.ok(savedArticleWarehouse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleWarehouse> getArticleWarehouseById(@PathVariable Long id) {
        Optional<ArticleWarehouse> articleWarehouse = articleWarehouseService.getArticleWarehouseById(id);
        return articleWarehouse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ArticleWarehouse>> getAllArticleWarehouses() {
        List<ArticleWarehouse> articleWarehouses = articleWarehouseService.getAllArticleWarehouses();
        return ResponseEntity.ok(articleWarehouses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticleWarehouseById(@PathVariable Long id) {
        articleWarehouseService.deleteArticleWarehouseById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProductPrice(@PathVariable long productId, @RequestBody Map<String, Double> requestBody) {
        try {
            if (requestBody.containsKey("purchasePrice")) {
                double newPrice = requestBody.get("purchasePrice");
                articleWarehouseService.updatePurchasePrice(productId, newPrice);
                return new ResponseEntity<>("Product price updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid request body", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update product price: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
