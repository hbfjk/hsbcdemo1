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
 * Verify that retry work when there is exception when saving transaction.
 * Also make sure the account is not updated when transaction fails.
 * @author fangj
 */
@SpringBootTest
public class TransactionServiceRetry2Test {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;
    
    private Account sourceAccount;
    private Account destinationAccount;
    
    @BeforeEach
    public void setUp() {
        sourceAccount = new Account("source123", new BigDecimal(1000.0));
        destinationAccount = new Account("dest123", new BigDecimal(500.0));
        
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }
    
    @Test
    void testProcessTransaction_retry() {
        
        Mockito.when(transactionRepository.save(any()))
                .thenThrow(new RuntimeException("transaction failed"))
                .thenThrow(new RuntimeException("transaction failed"))
                .thenThrow(new RuntimeException("transaction failed"));
        
        // Expecting exception due to non-exist account
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.processTransaction("source123", "dest123", new BigDecimal(100.0));
        });
        
        // Fetch updated accounts from the repository
        Optional<Account> updatedSourceAccountOpt = accountRepository.findById("source123");
        Optional<Account> updatedDestinationAccountOpt = accountRepository.findById("dest123");
        
        Account updatedSourceAccount = updatedSourceAccountOpt.get();
        Account updatedDestinationAccount = updatedDestinationAccountOpt.get();
        
        //verify teh source and destination account is not updated.
        assertEquals(new BigDecimal("1000.00"), updatedSourceAccount.getBalance(), "Source account balance should NOT be updated");
        assertEquals(new BigDecimal("500.00"), updatedDestinationAccount.getBalance(), "Destination account balance should be NOT updated");

        assertTrue(exception.getMessage().contains("transaction failed"));
        
        verify(transactionRepository, times(3)).save(any());
    }
}
