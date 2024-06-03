package com.ERP.app.goods.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;


@Entity
public class ArticleWarehouse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    private double purchasePrice;


    public ArticleWarehouse() {
    }

    public ArticleWarehouse(Product product, double purchasePrices) {
        this.product = product;
        this.purchasePrice = purchasePrices;
    }

    public ArticleWarehouse(Product product) {
        this.product = product;

    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        BigDecimal roundedPrice = BigDecimal.valueOf(purchasePrice).setScale(2, RoundingMode.HALF_UP);
        this.purchasePrice = roundedPrice.doubleValue();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
