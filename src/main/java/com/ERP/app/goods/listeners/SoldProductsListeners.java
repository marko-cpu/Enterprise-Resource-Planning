package com.ERP.app.goods.listeners;


import com.ERP.app.goods.data.Warehouse;
import com.ERP.app.goods.repository.ReservationRepository;
import com.ERP.app.goods.repository.WarehouseRepository;
import com.ERP.app.messaging.SoldProductMessage;
import com.ERP.app.sales.data.OrderProduct;
import com.ERP.app.sales.repository.OrderProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SoldProductsListeners {


    @Autowired
    OrderProductRepository orderProductRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    public void processSoldProductsMessage(SoldProductMessage soldProductMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("Module goods receives a message about selling products: \n");

        List<OrderProduct> orderProductsList = orderProductRepository.findOrderProducts(soldProductMessage.getOrderId());

        for (OrderProduct orderProduct : orderProductsList) {
            sb.append("Product ID: ").append(orderProduct.getProduct().getId())
                    .append(", Quantity: ").append(orderProduct.getQuantity()).append("\n");
        }
        for (OrderProduct orderProducts : orderProductsList) {
            List<Long> reservationIdOptional = reservationRepository.findReservationId(orderProducts.getProduct().getId(), orderProducts.getQuantity());
            List<Warehouse> wareHouseStateList = warehouseRepository.findStateOfWarehousesForProductId(orderProducts.getProduct().getId());
            int remaining = orderProducts.getQuantity();

            if (!wareHouseStateList.isEmpty()) {
                for (Warehouse warehouseState : wareHouseStateList) {
                    int warehouseQuantity = warehouseState.getQuantity();
                    int taken = 0;
                    if (remaining <= 0) break;
                    int remainingWarehouseQuantity = warehouseQuantity - remaining;
                    if (remainingWarehouseQuantity <= 0) {
                        taken = warehouseQuantity;
                        warehouseRepository.delete(warehouseState);
                    } else {
                        taken = remaining;
                        warehouseState.setQuantity(remainingWarehouseQuantity);
                        warehouseRepository.save(warehouseState);
                    }
                    remaining -= taken;
                }
            }
            if (!reservationIdOptional.isEmpty()) {
                for (Long reservationId : reservationIdOptional) {
                    reservationRepository.deleteById(reservationId);
                }
            }
        }
        System.out.println(sb);
    }
}
