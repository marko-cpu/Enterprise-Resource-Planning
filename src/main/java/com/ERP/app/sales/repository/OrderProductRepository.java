package com.ERP.app.sales.repository;

import com.ERP.app.sales.data.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query("SELECT op FROM OrderProduct op WHERE op.order.id=:orderId")
    List<OrderProduct> findOrderProducts(@Param("orderId") long orderId);

//    List<OrderProduct> findByOrderId(Long orderId);
}
