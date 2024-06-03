package com.ERP.app.sales.repository;

import com.ERP.app.sales.data.Accounting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountingRepository extends JpaRepository<Accounting, Long> {



    @Query("SELECT a FROM Accounting a WHERE a.state = 2")
    List<Accounting> findByStateTwo();

    @Modifying
    @Transactional
    @Query("DELETE FROM Accounting a WHERE a.id = :accountingId AND a.state = 2")
    void deleteByIdAndStateTwo(@Param("accountingId") Long accountingId);


    @Query("SELECT a FROM Accounting a WHERE a.date<=:currentDate AND a.state=0")
    List<Accounting> deadlinePassed(@Param("currentDate") LocalDate currentDate);
}

