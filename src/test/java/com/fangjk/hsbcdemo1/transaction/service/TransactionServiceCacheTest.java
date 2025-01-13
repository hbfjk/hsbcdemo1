/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.service;

import com.fangjk.hsbcdemo1.transaction.model.Account;
import com.fangjk.hsbcdemo1.transaction.model.Transaction;
import com.fangjk.hsbcdemo1.transaction.repository.AccountRepository;
import com.fangjk.hsbcdemo1.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Verify that retry work when there is exception when retrieving account.
 * @author fangj
 */
@SpringBootTest
public class TransactionServiceCacheTest {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private CacheManager cacheManager;  // Spring 缓存管理器
    
    private Account sourceAccount;
    private Account destinationAccount;
    private Transaction transaction;
    
    @BeforeEach
    void setUp() {
        // 清空缓存
        cacheManager.getCache("accountCache").clear();
        
        sourceAccount = new Account("source123", new BigDecimal(1000.0));
        destinationAccount = new Account("dest123", new BigDecimal(500.0));
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }
    
    @Test
    void testProcessTransaction_cache() {
        
        accountService.getAccountByNumber("source123");
        accountService.getAccountByNumber("dest123");
        
        //check cache
        Cache cache = cacheManager.getCache("accountCache");
        Object value = cache.get("source123");
        assertNotNull(value);
        value = cache.get("dest123");
        assertNotNull(value);
        
        accountService.deleteAccountByNumber("source123");
        accountService.deleteAccountByNumber("dest123");
        
        //check cache again
        Cache cache_after = cacheManager.getCache("accountCache");
        Object value_after = cache_after.get("source123");
        assertNull(value_after, "the source123 shouild be null.");
        value_after = cache.get("dest123");
        assertNull(value_after, "the dest123 shouild be null.");
        
    }
    
    @Test
    void testProcessTransaction_transfer_cache() {
        accountService.getAccountByNumber("source123");
        accountService.getAccountByNumber("dest123");
        
        //check cache
        Cache cache = cacheManager.getCache("accountCache");
        Object value = cache.get("source123");
        assertNotNull(value);
        value = cache.get("dest123");
        assertNotNull(value);
        
        transaction = transactionService.processTransaction("source123", "dest123", new BigDecimal(100.0));
        
        //check cache again
        Cache cache_after = cacheManager.getCache("accountCache");
        Object value_after = cache_after.get("source123");
        assertNull(value_after, "the source123 shouild be null.");
        value_after = cache.get("dest123");
        assertNull(value_after, "the dest123 shouild be null.");
        
    }
    
    @AfterEach
    public void tearDown() {
        accountRepository.delete(sourceAccount);
        accountRepository.delete(destinationAccount);
        
        if(transaction != null) {
            transactionRepository.delete(transaction);
        }
    }
}
