/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.service;

import com.fangjk.hsbcdemo1.transaction.model.Account;
import com.fangjk.hsbcdemo1.transaction.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 *
 * @author fangj
 */
@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    public Account findByAccountNumberForUpdate(String accountNumber) {
        return accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }
    
    @Cacheable(value = "accountCache", key = "#accountNumber")
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }
    
    @CacheEvict(value = "accountCache", key = "#accountNumber")
    public void deleteAccountByNumber(String accountNumber) {
        accountRepository.deleteById(accountNumber);
    }
    
}
