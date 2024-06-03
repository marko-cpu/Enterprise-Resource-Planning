package com.ERP.app.goods.listeners;

import com.ERP.app.goods.data.Reservation;
import com.ERP.app.goods.repository.ReservationRepository;
import com.ERP.app.messaging.ReservationCancellationMessage;
import com.ERP.app.messaging.ReservationResponseMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CancelReservationListeners {

    @Autowired
    ReservationRepository reservationRepository;


    public void cancelReservation(ReservationCancellationMessage reservationCancellationMessage) {

        System.out.println("Module goods receives a message about cancelling a reservation: " + reservationCancellationMessage.getOrderId());

        List<Reservation> reservationList = reservationRepository.findReservationsByOrderId(reservationCancellationMessage.getOrderId());

        if(!reservationList.isEmpty()) {
            for (Reservation reservation : reservationList) {
                reservationRepository.delete(reservation);
            }
        }


    }

}
