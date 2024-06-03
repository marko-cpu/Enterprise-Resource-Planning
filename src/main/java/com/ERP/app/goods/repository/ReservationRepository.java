package com.ERP.app.goods.repository;

import com.ERP.app.goods.data.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {



    @Query("SELECT SUM(r.quantity) FROM Reservation r JOIN r.product p WHERE p.id = :productId")
    Optional<Integer> findTotalReservedQuantityByProductId(@Param("productId") Long productId);

    @Query("SELECT r.id FROM Reservation r WHERE r.product.id = :productId AND r.quantity = :quantity")
    List<Long> findReservationId(@Param("productId") long productId, @Param("quantity") int quantity);

    @Query("SELECT r FROM Reservation r JOIN r.order o WHERE r.order.id = :orderId")
    List<Reservation> findReservationsByOrderId(@Param("orderId") long orderId);

}
