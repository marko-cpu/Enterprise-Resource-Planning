package com.ERP.app.goods.listeners;


import com.ERP.app.goods.data.Product;
import com.ERP.app.goods.data.Reservation;
import com.ERP.app.goods.repository.ReservationRepository;
import com.ERP.app.goods.repository.WarehouseRepository;
import com.ERP.app.messaging.ReservationMessage;
import com.ERP.app.messaging.ReservationResponseMessage;
import com.ERP.app.sales.data.Accounting;
import com.ERP.app.sales.data.OrderProduct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.ERP.app.config.RabbitMQConfigurator.PRODUCTS_TOPIC_EXCHANGE_NAME;


@Component
public class ReservationListeners {


    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void processReservation(ReservationMessage reservationMessage) {

        ReservationResponseMessage response = new ReservationResponseMessage();
        Accounting accounting = reservationMessage.getAccounting();
        boolean successful = true;

        for(OrderProduct orderProduct : reservationMessage.getProductsList()) {

            Product product = orderProduct.getProduct();
            Optional<Integer> quantityOptional = warehouseRepository.findTotalQuantityByProductId(product.getId());
            Optional<Integer> reservedQuantityOptional = reservationRepository.findTotalReservedQuantityByProductId(product.getId());

            int quantity = quantityOptional.orElse(0);
            int reservedQuantity = reservedQuantityOptional.orElse(0);

            int totalQuantity = quantity - reservedQuantity;
            int requestedQuantity = orderProduct.getQuantity();

            if(totalQuantity < requestedQuantity) {
                successful = false;
                response.setSuccessful(false);
                response.setMessage("Couldn't make a reservation for product with id: " + product.getId());
                rabbitTemplate.convertAndSend(PRODUCTS_TOPIC_EXCHANGE_NAME, "reservationresponse.queue", response);
            }
        }

        if(successful) {
            for(OrderProduct product : reservationMessage.getProductsList()) {
                Reservation reservation = new Reservation(product.getProduct(), product.getQuantity(), accounting.getOrder());
                reservationRepository.save(reservation);
            }
            response.setSuccessful(true);
            response.setMessage("Reservation successful!");
            response.setAccounting(accounting);
            rabbitTemplate.convertAndSend(PRODUCTS_TOPIC_EXCHANGE_NAME, "reservationresponse.queue", response);
        }
    }

}
