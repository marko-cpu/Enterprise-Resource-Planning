package com.ERP.app.goods.services;

import com.ERP.app.goods.data.ArticleWarehouse;
import com.ERP.app.goods.data.Product;
import com.ERP.app.goods.repository.ArticleWarehouseRepository;
import com.ERP.app.messaging.ProductMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ERP.app.config.RabbitMQConfigurator.PRODUCTS_TOPIC_EXCHANGE_NAME;

@Service
public class ArticleWarehouseService {


    @Autowired
    private ArticleWarehouseRepository articleWarehouseRepository;

    @Autowired
    private  RabbitTemplate rabbitTemplate;

    @Transactional
    public ArticleWarehouse saveArticleWarehouse(ArticleWarehouse articleWarehouse) {
        return articleWarehouseRepository.save(articleWarehouse);
    }

    @Transactional(readOnly = true)
    public Optional<ArticleWarehouse> getArticleWarehouseById(Long id) {
        return articleWarehouseRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ArticleWarehouse> getAllArticleWarehouses() {
        return articleWarehouseRepository.findAll();
    }

    @Transactional
    public void deleteArticleWarehouseById(Long id) {
        articleWarehouseRepository.deleteById(id);
    }

    @Transactional
    public void updatePurchasePrice(long productId, double newPrice) {
        ArticleWarehouse articleWarehouse = articleWarehouseRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        articleWarehouse.setPurchasePrice(newPrice);
        articleWarehouseRepository.save(articleWarehouse);

        List<Product> updatedProducts = Collections.singletonList(articleWarehouse.getProduct());


        ProductMessage productEventMessage = ProductMessage.updatePriceOfProduct(updatedProducts);
        rabbitTemplate.convertAndSend(PRODUCTS_TOPIC_EXCHANGE_NAME,
                "product.updatePrice",productEventMessage);
    }



}
