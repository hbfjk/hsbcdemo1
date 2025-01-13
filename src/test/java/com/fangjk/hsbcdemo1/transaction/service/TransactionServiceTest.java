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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 *
 * @author fangj
 */
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private CacheManager cacheManager;
    
    @Mock
    private Cache cache;

    @InjectMocks
    private TransactionService transactionService;
    
    @Mock
    private AccountService accountService;
    
    private Account sourceAccount;
    private Account destinationAccount;
    
    @BeforeEach
    public void setUp() {
        sourceAccount = new Account("source", new BigDecimal(1000.0));
        destinationAccount = new Account("destination", new BigDecimal(500.0));
    }
    
    @Test
    public void testProcessTransaction_Success() throws Exception {
        String sourceAccountNumber = "source";
        String destination = "destination";
        BigDecimal ammount = new BigDecimal(200.0);

        when(accountService.findByAccountNumberForUpdate("source")).thenReturn(sourceAccount);
        when(accountService.findByAccountNumberForUpdate("destination")).thenReturn(destinationAccount);
        when(cacheManager.getCache("accountCache")).thenReturn(cache);

        transactionService.processTransaction(sourceAccountNumber,destination,ammount);
        
        assertEquals(new BigDecimal(800.0), sourceAccount.getBalance(), "Source account balance should be reduced.");
        assertEquals(new BigDecimal(700.0), destinationAccount.getBalance(), "Destination account balance should be increased.");
    }
    
    @Test
    void testProcessTransaction_AccountNotFound() {
        String sourceAccountNumber = "source";
        String destination = "destination";
        BigDecimal ammount = new BigDecimal(2000.0);

        // Mock repository behavior
        when(accountService.findByAccountNumberForUpdate("source")).thenThrow(new IllegalArgumentException("Account not found"))
;
        // Expecting exception due to insufficient funds
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.processTransaction(sourceAccountNumber,destination,ammount);
        });

        assertTrue(exception.getMessage().contains("Account not found"));
    }
    
    @Test
    void testProcessTransaction_InsufficientFunds() {
        String sourceAccountNumber = "source";
        String destination = "destination";
        BigDecimal ammount = new BigDecimal(2000.0);

        // Mock repository behavior
        when(accountService.findByAccountNumberForUpdate("source")).thenReturn(sourceAccount);
        when(accountService.findByAccountNumberForUpdate("destination")).thenReturn(destinationAccount);
        when(cacheManager.getCache("accountCache")).thenReturn(cache);

        // Expecting exception due to insufficient funds
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.processTransaction(sourceAccountNumber,destination,ammount);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }
    
}
