package com.ERP.app.goods.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Warehouse implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int warehouseId;
    private int supplierId;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private ArticleWarehouse product;

    private int quantity;
    private LocalDate date;





    public Warehouse() {
    }



    public Warehouse(int warehouseId, ArticleWarehouse article, int quantity, int supplierId, LocalDate date) {
        this.warehouseId = warehouseId;
        this.product = article;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public ArticleWarehouse getProduct() {
        return product;
    }

    public void setProduct(ArticleWarehouse product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }




}
