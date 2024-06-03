package com.ERP.app.sales.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
public class Invoice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "accounting_id")
    private Accounting accounting;
    private Double totalPrice;
    private LocalDate payDate;


    public Invoice() {
    }

    public Invoice(Accounting accounting, Double totalPrice, LocalDate payDate) {
        this.accounting = accounting;
        this.totalPrice = totalPrice;
        this.payDate = payDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        BigDecimal roundedPrice = BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP);
        this.totalPrice = roundedPrice.doubleValue();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Accounting getAccounting() {
        return accounting;
    }

    public void setAccounting(Accounting accounting) {
        this.accounting = accounting;
    }

    public LocalDate getPayDate() {
        return payDate;
    }

    public void setPayDate(LocalDate payDate) {
        this.payDate = payDate;
    }
}
