package com.ERP.app.sales.listeners;

import com.ERP.app.messaging.ReservationResponseMessage;
import com.ERP.app.sales.repository.AccountingRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationResponseListeners {

    @Autowired
    AccountingRepository accountingRepository;

    public void processReservationResponse(ReservationResponseMessage response) {

            if (response.isSuccessful()) {
                accountingRepository.save(response.getAccounting());
                System.out.println("Module Sales receives a message: " + response.getMessage());

            } else {
                System.out.println(response.getMessage());
            }
    }

}
