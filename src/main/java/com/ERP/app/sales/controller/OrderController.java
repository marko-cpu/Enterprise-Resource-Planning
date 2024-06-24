package com.ERP.app.sales.controller;


import com.ERP.app.sales.data.Order;
import com.ERP.app.sales.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;



    @GetMapping("/getOrders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/orders")
    public ResponseEntity<String>  createOrder(@RequestBody Order order) {

        try {
            orderService.createOrder(order);
            return ResponseEntity.ok("Order in progress");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding order: " + e.getMessage());

        }

    }

    @PostMapping("/pay")
    public ResponseEntity<String> addInvoice(@RequestBody Map<String, Object> requestBody) {
        try {
            double totalPrice = (double) requestBody.get("totalPrice");
            int id = (int) requestBody.get("accounting_id");
            orderService.addInvoice(id, totalPrice);
            return ResponseEntity.ok("Invoice added");
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding invoice: " + e.getMessage());
        }

    }



}
