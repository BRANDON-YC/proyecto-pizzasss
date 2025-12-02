package com.platzi.pizza.service;

import com.platzi.pizza.persitence.entity.OrderEntity;
import com.platzi.pizza.persitence.entity.OrderItemEntity;
import com.platzi.pizza.persitence.projection.OrderSummary;
import com.platzi.pizza.persitence.repository.OrderItemRepository;
import com.platzi.pizza.persitence.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private static final String DELIVERY = "D";
    private static final String CARRYOUT = "C";
    private static final String ON_SITE = "S";

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // 🔹 LISTAR TODOS
    public List<OrderEntity> getAll() {
        return this.orderRepository.findAll();
    }

    // 🔹 CREAR pedido (con pizzas)
    @Transactional
    public OrderEntity createOrder(OrderEntity order) {
        // ID nuevo
        order.setIdOrder(null);

        // Fecha si no viene del frontend
        if (order.getDate() == null) {
            order.setDate(LocalDateTime.now());
        }

        // Calcular total a partir de los items (pizza * cantidad)
        double total = 0.0;
        if (order.getItems() != null) {
            for (OrderItemEntity item : order.getItems()) {
                if (item.getQuantity() == null) {
                    item.setQuantity(1.0);
                }
                if (item.getPrice() == null) {
                    item.setPrice(0.0);
                }
                total += item.getPrice() * item.getQuantity();
            }
        }
        order.setTotal(total);

        // Primero guardamos el pedido para obtener id_order
        OrderEntity saved = this.orderRepository.save(order);

        // Luego guardamos los items ligados al pedido
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            int index = 1;
            for (OrderItemEntity item : order.getItems()) {
                item.setIdOrder(saved.getIdOrder());
                item.setIdItem(index++);
                // idPizza, quantity y price vienen del frontend
                this.orderItemRepository.save(item);
            }
        }

        return saved;
    }

    // 🔹 EDITAR pedido (solo datos generales: método, notas, total)
    @Transactional
    public OrderEntity updateOrder(int id, OrderEntity newData) {
        OrderEntity existing = this.orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el pedido con id " + id));

        existing.setIdCustomer(newData.getIdCustomer());
        existing.setMethod(newData.getMethod());
        existing.setAdditionalNotes(newData.getAdditionalNotes());

        if (newData.getDate() != null) {
            existing.setDate(newData.getDate());
        }
        if (newData.getTotal() != null) {
            existing.setTotal(newData.getTotal());
        }

        return this.orderRepository.save(existing);
    }

    // 🔹 ELIMINAR (primero hijos, luego padre)
    @Transactional
    public void deleteOrder(int id) {
        if (!this.orderRepository.existsById(id)) {
            return;
        }

        // 1️⃣ Borrar todos los items del pedido
        this.orderItemRepository.deleteByIdOrder(id);

        // 2️⃣ Ahora sí, borrar el pedido
        this.orderRepository.deleteById(id);
    }

    // 🔹 SOLO pedidos de HOY
    public List<OrderEntity> getTodayOrders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return this.orderRepository.findAllByDateAfter(startOfDay);
    }

    // 🔹 Pedidos FUERA (delivery / carry out)
    public List<OrderEntity> getOutsideOrders() {
        List<String> methods = Arrays.asList(DELIVERY, CARRYOUT);
        return this.orderRepository.findAllByMethodIn(methods);
    }

    // 🔹 Pedidos de un cliente
    public List<OrderEntity> getCustomerOrders(String idCustomer) {
        return this.orderRepository.findCustomerOrders(idCustomer);
    }

    // 🔹 Resumen con pizzas (usa el JOIN que ya tienes en el repo)
    public OrderSummary getSummary(int orderId) {
        return this.orderRepository.findSummary(orderId);
    }
}
