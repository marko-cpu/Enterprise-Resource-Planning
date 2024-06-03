package com.ERP.app.goods.repository;


import com.ERP.app.goods.data.ArticleWarehouse;
import com.ERP.app.goods.data.Product;
import com.ERP.app.goods.data.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ArticleWarehouseRepository extends JpaRepository<ArticleWarehouse,Long> {

    @Query("SELECT aw from ArticleWarehouse aw WHERE aw.product=:productId AND aw.purchasePrice=:purchasePrice")
    Optional<ArticleWarehouse> findArticleWarehouse(@Param("productId") Product productId, @Param("purchasePrice") double purchasePrice);



}
