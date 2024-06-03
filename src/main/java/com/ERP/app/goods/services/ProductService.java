package com.ERP.app.goods.services;

import com.ERP.app.goods.data.ArticleWarehouse;
import com.ERP.app.goods.data.Product;
import com.ERP.app.goods.data.Warehouse;
import com.ERP.app.goods.repository.ProductRepository;
import com.ERP.app.goods.repository.ReservationRepository;
import com.ERP.app.goods.repository.WarehouseRepository;
import com.ERP.app.messaging.ProductMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ERP.app.config.RabbitMQConfigurator.PRODUCTS_TOPIC_EXCHANGE_NAME;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ReservationRepository reservationRepository;



    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Product addProduct(Product product) {
        productRepository.save(product);
        ProductMessage productEventMessage = ProductMessage.createNewProduct(product);
        rabbitTemplate.convertAndSend(PRODUCTS_TOPIC_EXCHANGE_NAME,
                "product.create",productEventMessage);
        return product;
    }

    public void receptionOfProducts(int supplierId, int warehouseId, int quantity, List<ArticleWarehouse> articles) {
        LocalDate date = LocalDate.now();
        List<Product> products = new ArrayList<>();

        for(ArticleWarehouse article : articles) {
            products.add(article.getProduct());
            Warehouse warehouse = new Warehouse(warehouseId,article,quantity,supplierId,date);
            warehouseRepository.save(warehouse);
        }

        ProductMessage productEventMessage = ProductMessage.updateStateOfProduct(products);
        rabbitTemplate.convertAndSend(PRODUCTS_TOPIC_EXCHANGE_NAME,
                "product.updateState",productEventMessage);
    }




      public String getProductData(long productId) {

          StringBuilder sb = new StringBuilder();
          Product product = productRepository.findById(productId).orElseThrow();
          sb.append("Product: \n").append(product.getProductName()).append("\n");

          Optional<Integer> quantity = warehouseRepository.findTotalQuantityByProductId(productId);
          Optional<Integer> reservedQuantity = reservationRepository.findTotalReservedQuantityByProductId(productId);
          int totalQauntity = reservedQuantity.map(integer -> quantity.get() - integer).orElseGet(quantity::get);
          sb.append("Total quantity: ").append(totalQauntity).append("\n");

          List<Warehouse> warehousePurchase = warehouseRepository.findStateOfWarehousesForProductId(productId);
          for(Warehouse w : warehousePurchase) {
              sb.append("Price: ").append(w.getProduct().getPurchasePrice())
                      .append(", Supplier id: ").append(w.getSupplierId())
                      .append(", Date: ").append(w.getDate()).append("\n");
          }
          return sb.toString();


      }

    public String getProductState(long productId) {
        StringBuilder sb = new StringBuilder();
        sb.append("WarehouseID | quantity ");
        List<Object[]> result = warehouseRepository.findQuantityForProductIdGroupByWarehouse(productId);

        for(Object[] o : result) {
            sb.append(o[0]).append(" | ").append(o[1]).append("\n");
        }
        return sb.toString();
    }


}



