package com.ERP.app.sales.services;

import com.ERP.app.goods.data.ArticleWarehouse;
import com.ERP.app.goods.data.Reservation;
import com.ERP.app.goods.data.Warehouse;
import com.ERP.app.goods.repository.ArticleWarehouseRepository;
import com.ERP.app.goods.repository.ReservationRepository;
import com.ERP.app.goods.repository.WarehouseRepository;
import com.ERP.app.messaging.ReservationCancellationMessage;
import com.ERP.app.messaging.ReservationMessage;
import com.ERP.app.messaging.SoldProductMessage;
import com.ERP.app.sales.data.*;
import com.ERP.app.sales.repository.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ERP.app.config.RabbitMQConfigurator.ORDERS_TOPIC_EXCHANGE_NAME;

@Service
public class OrderService {


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderProductRepository orderProductRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    ArticleWarehouseRepository articleWarehouseRepository;

    @Autowired
    private AccountingRepository accountingRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;



    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void createOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }


        Customer customer = order.getCustomer();
        if (customer == null) {
            System.out.println(customer);
            throw new IllegalArgumentException("Order must have a customer");
        }

        String customerEmail = customer.getEmail();
        if (customerEmail != null && !customerEmail.isEmpty()) {
            customer = customerRepository.findByEmail(customerEmail);
        }

        // Ako kupac ne postoji, kreiranje novog koristeći podatke iz narudžbine
        if (customer == null) {
            customer = new Customer();
            customer.setFirstName(order.getCustomer().getFirstName());
            customer.setLastName(order.getCustomer().getLastName());
            customer.setEmail(order.getCustomer().getEmail());
            customer.setAddress(order.getCustomer().getAddress());
            customer.setPhone(order.getCustomer().getPhone());
            customerRepository.save(customer);
        }


        order.setCustomer(customer);


        // System.out.println("Creating order for customer: " + order.getCustomerName());


        if (order.getProductList() == null || order.getProductList().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product");
        }

        this.orderRepository.save(order);

        double totalPrice = 0.0;
        LocalDate dateOfPayment = LocalDate.now().plusDays(5);
        List<OrderProduct> productList = order.getProductList();

        for (int i = 0; i < productList.size(); i++) {
            OrderProduct orderProduct = productList.get(i);
            if (orderProduct.getProduct() == null) {
                throw new IllegalArgumentException("Product in OrderProduct cannot be null");
            }

            int count = 0;
            double purchasePrice = 0.0;

            for (Warehouse w : warehouseRepository.findStateOfWarehousesForProductId(orderProduct.getProduct().getId())) {
                ++count;
                purchasePrice += w.getProduct().getPurchasePrice();
            }

            if (count > 0) {
                purchasePrice /= count;
            } else {
                throw new RuntimeException("No warehouses found for product ID: " + orderProduct.getProduct().getId());
            }

            orderProduct.setPricePerUnit(purchasePrice);
            orderProduct.setTotalPrice((orderProduct.getPricePerUnit() + (orderProduct.getPdv() * orderProduct.getPricePerUnit())) * orderProduct.getQuantity());
            orderProduct.setOrder(order);
            this.orderProductRepository.save(orderProduct);
            totalPrice += orderProduct.getTotalPrice();
        }

        Accounting tmpAccounting = new Accounting(order, dateOfPayment, totalPrice);
        ReservationMessage reservationMessage = new ReservationMessage(productList, tmpAccounting);
        rabbitTemplate.convertAndSend(ORDERS_TOPIC_EXCHANGE_NAME, "reservation.queue", reservationMessage);


    //    System.out.println("Order created successfully with total price: " + totalPrice);
    }


    public Invoice addInvoice(long accountingId, double totalPrice) throws Exception {
        Optional<Accounting> accountingOptional = accountingRepository.findById(accountingId);
        try {
            Accounting accounting = accountingOptional.orElseThrow(() -> new Exception("Accounting id does not exist!"));

            if (accounting.getState() == 1) {
                throw new Exception("Order is already paid!");
            }
        /*    if(accounting.getState() == 2){
                accountingRepository.deleteById(accountingId);
                throw new Exception("Order is already cancelled!");
            }*/
            if (accounting.getTotalPrice() > totalPrice) {
                throw new Exception("Not enough money!");
            }

            accounting.setState((short) 1);
            accountingRepository.save(accounting);

            LocalDate payDate = LocalDate.now();
            Invoice invoice = new Invoice(accounting, totalPrice, payDate);
            invoiceRepository.save(invoice);

            SoldProductMessage soldProductMessage = new SoldProductMessage(invoice.getAccounting().getOrder().getId());
            rabbitTemplate.convertAndSend(ORDERS_TOPIC_EXCHANGE_NAME,
                    "soldproducts.queue", soldProductMessage);
            return invoice;
        } catch (Exception e) {
            throw e;
        }
    }

   // @Scheduled(cron = "0 0 9 * * MON-FRI")
    @PostConstruct
    public void dailyCheckAccountings() {

        checkAccountings();
        deleteCancelledAccountings();


    }



    @Transactional
    private void deleteCancelledAccountings() {
        List<Accounting> accountings = accountingRepository.findByStateTwo();
        if (accountings.isEmpty()) {
            return;
        }

        for (Accounting accounting : accountings) {
            try {
                accountingRepository.deleteByIdAndStateTwo(accounting.getId());
                logger.info("Deleted accounting with ID: {}", accounting.getId());
            } catch (Exception e) {
                logger.error("Error deleting accounting with ID: {}", accounting.getId(), e);
            }
        }
    }


    public void checkAccountings() {
        LocalDate date = LocalDate.now();
        List<Accounting> accountings = accountingRepository.deadlinePassed(date);
        if (!accountings.isEmpty()) {
            for (Accounting accounting : accountings) {
                try {
                    accounting.setState((short) 2);
                    accountingRepository.save(accounting);
                    long orderId = accounting.getOrder().getId();
                    ReservationCancellationMessage reservationCancellation = new ReservationCancellationMessage(orderId);
                    rabbitTemplate.convertAndSend(ORDERS_TOPIC_EXCHANGE_NAME, "cancelreservation.queue", reservationCancellation);


                    logger.info("Canceled accounting with Order ID: {}", accounting.getOrder().getId());
                } catch (Exception e) {
                    logger.error("Error canceling accounting with Order ID: {}", accounting.getOrder().getId(), e);
                }
            }
        }
    }



}


