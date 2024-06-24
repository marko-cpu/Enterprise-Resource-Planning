package com.ERP.app.sales.services;

import com.ERP.app.sales.data.Accounting;
import com.ERP.app.sales.repository.AccountingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AccountingService {

    private final AccountingRepository accountingRepository;

    @Autowired
    public AccountingService(AccountingRepository accountingRepository) {
        this.accountingRepository = accountingRepository;
    }

    public List<Accounting> getAllAccountings() {
        return accountingRepository.findAll();
    }



    public void deleteAccounting(Long id) {
        accountingRepository.deleteById(id);
    }

    public List<Accounting> findAccountingsByStateTwo() {
        return accountingRepository.findByStateTwo();
    }

    public void deleteByIdAndStateTwo(Long id) {
        accountingRepository.deleteByIdAndStateTwo(id);
    }





}
