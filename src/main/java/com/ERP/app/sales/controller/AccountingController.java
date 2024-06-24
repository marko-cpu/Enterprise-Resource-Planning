package com.ERP.app.sales.controller;

import com.ERP.app.sales.data.Accounting;
import com.ERP.app.sales.services.AccountingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accountings")
public class AccountingController {

    private final AccountingService accountingService;

    @Autowired
    public AccountingController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @GetMapping
    public List<Accounting> getAllAccountings() {
        return accountingService.getAllAccountings();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccounting(@PathVariable Long id) {
        accountingService.deleteAccounting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/state-two")
    public List<Accounting> findAccountingsByStateTwo() {
        return accountingService.findAccountingsByStateTwo();
    }


}
