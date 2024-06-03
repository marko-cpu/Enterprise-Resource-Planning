package com.ERP.app.goods.controller;

import com.ERP.app.goods.data.ArticleWarehouse;
import com.ERP.app.goods.data.Product;
import com.ERP.app.goods.data.Warehouse;
import com.ERP.app.goods.services.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping
    public List<Warehouse> getAllWarehouses() {
        return warehouseService.getAllWarehouses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable Long id) {
        Optional<Warehouse> warehouse = warehouseService.getWarehouseById(id);
        return warehouse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> addToWarehouse(@RequestBody Warehouse warehouse) {
        warehouseService.addToWarehouse(warehouse);
        return ResponseEntity.ok("Products successfully added to warehouse");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok("" + id + " deleted successfully");
    }
}