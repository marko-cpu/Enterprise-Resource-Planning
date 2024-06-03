package com.ERP.app.sales.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;


@Entity
public class Accounting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private LocalDate date;
    private double totalPrice;
    private short state = 0;

    public Accounting(Order order, LocalDate date,double totalPrice) {
        this.order = order;
        this.date = date;
        this.totalPrice = totalPrice;
    }


    public Accounting() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        BigDecimal roundedPrice = BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP);
        this.totalPrice = roundedPrice.doubleValue();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Accounting{");
        sb.append("id=").append(id);
        sb.append(", order=").append(order);
        sb.append(", date=").append(date);
        sb.append(", totalPrice=").append(totalPrice);
        sb.append(", state=").append(state);
        sb.append('}');
        return sb.toString();
    }
}
