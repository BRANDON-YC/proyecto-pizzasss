package com.platzi.pizza.web.controller;

import com.platzi.pizza.persitence.entity.OrderEntity;
import com.platzi.pizza.persitence.projection.OrderSummary;
import com.platzi.pizza.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderEntity>> getAll() {
        return ResponseEntity.ok(this.orderService.getAll());
    }

    @GetMapping("/today")
    public ResponseEntity<List<OrderEntity>> getTodayOrders() {
        return ResponseEntity.ok(this.orderService.getTodayOrders());
    }

    @GetMapping("/outside")
    public ResponseEntity<List<OrderEntity>> getOutsideOrders() {
        return ResponseEntity.ok(this.orderService.getOutsideOrders());
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<OrderEntity>> getOutsideOrders(@PathVariable String id) {
        return ResponseEntity.ok(this.orderService.getCustomerOrders(id));
    }

    @GetMapping("/summary/{id}")
    public ResponseEntity<OrderSummary> getSummary(@PathVariable int id) {
        return ResponseEntity.ok(this.orderService.getSummary(id));
    }

    // 🔹 CREAR pedido (POST /api/orders)
    @PostMapping
    public ResponseEntity<OrderEntity> create(@RequestBody OrderEntity order) {
        OrderEntity saved = this.orderService.createOrder(order);
        return ResponseEntity.ok(saved);
    }

    // 🔹 EDITAR pedido (PUT /api/orders/{id})
    @PutMapping("/{id}")
    public ResponseEntity<OrderEntity> update(@PathVariable int id,
                                              @RequestBody OrderEntity order) {
        OrderEntity updated = this.orderService.updateOrder(id, order);
        return ResponseEntity.ok(updated);
    }

    // 🔹 ELIMINAR pedido (DELETE /api/orders/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        this.orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
