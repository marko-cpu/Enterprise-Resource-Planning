package com.ERP.app.goods.services;

import com.ERP.app.goods.data.Warehouse;
import com.ERP.app.goods.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    @Transactional
    public Warehouse addToWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @Transactional
    public Warehouse updateWarehouse(Long id, Warehouse updatedWarehouse) {
        return warehouseRepository.findById(id).map(warehouse -> {
            warehouse.setWarehouseId(updatedWarehouse.getWarehouseId());
            warehouse.setSupplierId(updatedWarehouse.getSupplierId());
            warehouse.setProduct(updatedWarehouse.getProduct());
            warehouse.setQuantity(updatedWarehouse.getQuantity());
            warehouse.setDate(updatedWarehouse.getDate());
            return warehouseRepository.save(warehouse);
        }).orElseGet(() -> {
            updatedWarehouse.setId(id);
            return warehouseRepository.save(updatedWarehouse);
        });
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        warehouseRepository.deleteById(id);
    }

}
