package com.platzi.pizza.persitence.repository;

import com.platzi.pizza.persitence.entity.OrderItemEntity;
import com.platzi.pizza.persitence.entity.OrderItemId;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrderItemRepository extends ListCrudRepository<OrderItemEntity, OrderItemId> {

    // Para consultar los items de un pedido
    List<OrderItemEntity> findByIdOrder(Integer idOrder);

    // 🔥 Para borrar TODOS los items de un pedido
    void deleteByIdOrder(Integer idOrder);
}
