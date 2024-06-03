package com.ERP.app.sales.data;

import com.ERP.app.goods.data.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class OrderProduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    private double pricePerUnit;
    private double pdv;
    private double totalPrice;
    private int quantity;

    public OrderProduct(long id, Product product, double pricePerUnit, double pdv, int quantity, double totalPrice) {
        this.id = id;
        this.product = product;
        this.pricePerUnit = pricePerUnit;
        this.pdv = pdv;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public OrderProduct(Order order,Product product, double pdv, int quantity) {
        this.order = order;
        this.product = product;
        this.pdv = pdv;
        this.quantity = quantity;
        this.pricePerUnit = 25.5;
        this.totalPrice = (pricePerUnit + pdv) * quantity;
    }

    public OrderProduct() {
    }

    public OrderProduct(Product product, double pricePerUnit, double pdv, int quantity) {
        this.product = product;
        this.pricePerUnit = pricePerUnit;
        this.pdv = pdv;
        this.quantity = quantity;
        this.totalPrice = quantity * (pricePerUnit + pdv);
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public double getPdv() {
        return pdv;
    }

    public void setPdv(double pdv) {
        this.pdv = pdv;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        BigDecimal roundedPrice = BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP);
        this.totalPrice = roundedPrice.doubleValue();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}


