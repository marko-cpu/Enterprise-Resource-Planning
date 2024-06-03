package com.ERP.app.config;

import com.ERP.app.goods.listeners.CancelReservationListeners;
import com.ERP.app.goods.listeners.ReservationListeners;
import com.ERP.app.goods.listeners.SoldProductsListeners;
import com.ERP.app.sales.listeners.ProductEventListener;
import com.ERP.app.sales.listeners.ReservationResponseListeners;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfigurator {

    public static final String PRODUCTS_TOPIC_EXCHANGE_NAME = "products-exchange";
    public static final String ORDERS_TOPIC_EXCHANGE_NAME = "orders-exchange";

    @Bean
    TopicExchange productExchange() {
        return new TopicExchange(PRODUCTS_TOPIC_EXCHANGE_NAME);
    }
    @Bean
    TopicExchange ordersExchange() {
        return new TopicExchange(ORDERS_TOPIC_EXCHANGE_NAME);
    }

    public static final String PRODUCTS_QUEUE = "products-queue";
    public static final String RESERVATION_QUEUE = "reservation-queue";
    public static final String RESERVATION_RESPONSE_QUEUE = "reservation-response-queue";
    public static final String CANCEL_RESERVATION_QUEUE = "cancel-reservation-queue";
    public static final String SOLD_PRODUCTS_QUEUE = "sold-products-queue";

    // Queues
    @Bean
    Queue productQueue() {
        return new Queue(PRODUCTS_QUEUE, false);
    }
    @Bean
    Queue reservationQueue() {
        return new Queue(RESERVATION_QUEUE, false);
    }
    @Bean
    Queue reservationResponseQueue() {
        return new Queue(RESERVATION_RESPONSE_QUEUE, false);
    }
    @Bean
    Queue soldProductsQueue() {
        return new Queue(SOLD_PRODUCTS_QUEUE, false);
    }
    @Bean
    Queue cancelReservationQueue() {
        return new Queue(CANCEL_RESERVATION_QUEUE, false);
    }


    // Bindings
    @Bean
    Binding productBinding(Queue productQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(productQueue).to(productExchange).with("product.*");
    }
    @Bean
    Binding reservationBinding(Queue reservationQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(reservationQueue).to(ordersExchange).with("reservation.*");
    }

    @Bean
    Binding reservationResponseBinding(Queue reservationResponseQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(reservationResponseQueue).to(productExchange).with("reservationresponse.*");
    }

    @Bean
    Binding soldProductsBinding(Queue soldProductsQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(soldProductsQueue).to(ordersExchange).with("soldproducts.*");
    }
    @Bean
    Binding cancelReservationBinding(Queue cancelReservationQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(cancelReservationQueue).to(ordersExchange).with("cancelreservation.*");
    }


    // Containers
    @Bean
    SimpleMessageListenerContainer productEventListenerContainer(ConnectionFactory connectionFactory,
                                                                 MessageListenerAdapter productEventListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(PRODUCTS_QUEUE);
        container.setMessageListener(productEventListenerAdapter);
        return container;
    }
    @Bean
    SimpleMessageListenerContainer reservationListenerContainer(ConnectionFactory connectionFactory,
                                                                MessageListenerAdapter reservationListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(RESERVATION_QUEUE);
        container.setMessageListener(reservationListenerAdapter);
        return container;
    }
    @Bean
    SimpleMessageListenerContainer reservationResponseListenerContainer(ConnectionFactory connectionFactory,
                                                                        MessageListenerAdapter reservationResponseListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(RESERVATION_RESPONSE_QUEUE);
        container.setMessageListener(reservationResponseListenerAdapter);
        return container;
    }
    @Bean
    SimpleMessageListenerContainer soldProductsListenerContainer(ConnectionFactory connectionFactory,
                                                                 MessageListenerAdapter soldProductsListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(SOLD_PRODUCTS_QUEUE);
        container.setMessageListener(soldProductsListenerAdapter);
        return container;
    }
    @Bean
    SimpleMessageListenerContainer cancelReservationListenerContainer(ConnectionFactory connectionFactory,
                                                                      MessageListenerAdapter cancelReservationListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(CANCEL_RESERVATION_QUEUE);
        container.setMessageListener(cancelReservationListenerAdapter);
        return container;
    }

    // Converters for JSON
    @Bean
    public MessageConverter jsonToMapMessageConverter() {
        DefaultClassMapper defaultClassMapper = new DefaultClassMapper();
        defaultClassMapper.setTrustedPackages("*");
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setClassMapper(defaultClassMapper);
        return jackson2JsonMessageConverter;
    }


    //adapteri
    @Bean
    MessageListenerAdapter productEventListenerAdapter(ProductEventListener productEventListener, MessageConverter messageConverter) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(productEventListener, "processProductEvent");
        adapter.setMessageConverter(messageConverter);
        return adapter;
    }
    @Bean
    MessageListenerAdapter reservationListenerAdapter(ReservationListeners reservationListener, MessageConverter messageConverter) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(reservationListener, "processReservation");
        adapter.setMessageConverter(messageConverter);
        return adapter;
    }

    @Bean
    MessageListenerAdapter reservationResponseListenerAdapter(ReservationResponseListeners reservationResponseListener, MessageConverter messageConverter) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(reservationResponseListener, "processReservationResponse");
        adapter.setMessageConverter(messageConverter);
        return adapter;
    }
    @Bean
    MessageListenerAdapter soldProductsListenerAdapter(SoldProductsListeners soldProductsListener, MessageConverter messageConverter) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(soldProductsListener, "processSoldProductsMessage");
        adapter.setMessageConverter(messageConverter);
        return adapter;
    }
    @Bean
    MessageListenerAdapter cancelReservationListenerAdapter(CancelReservationListeners cancelReservationListener, MessageConverter messageConverter) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(cancelReservationListener, "cancelReservation");
        adapter.setMessageConverter(messageConverter);
        return adapter;
    }



}
