/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.service;

import com.fangjk.hsbcdemo1.transaction.model.Account;
import com.fangjk.hsbcdemo1.transaction.repository.AccountRepository;
import com.fangjk.hsbcdemo1.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Verify that retry work when there is exception when retrieving account.
 * @author fangj
 */
@SpringBootTest
public class TransactionServiceRetryTest {
    
    @MockBean
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionService transactionService;
    
    @Test
    void testProcessTransaction_retry() {
        
        // Expecting exception due to non-exist account
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.processTransaction("source123", "dest123", new BigDecimal(100.0));
        });
        
        assertTrue(exception.getMessage().contains("Account not found"));
        
        verify(accountRepository, times(3)).findByAccountNumberForUpdate("source123");
    }
}
