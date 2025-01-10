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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author fangj
 */
@SpringBootTest
public class TransactionServiceIntegrationTest {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
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
    void testProcessTransaction_Success() {
        // Process a valid transaction
        transactionService.processTransaction("source123", "dest123", new BigDecimal(100.0));

        // Fetch updated accounts from the repository
        Optional<Account> updatedSourceAccountOpt = accountRepository.findById("source123");
        Optional<Account> updatedDestinationAccountOpt = accountRepository.findById("dest123");
        
        Account updatedSourceAccount = updatedSourceAccountOpt.get();
        Account updatedDestinationAccount = updatedDestinationAccountOpt.get();

        // Assert that the balances were correctly updated
        assertEquals(new BigDecimal("900.00"), updatedSourceAccount.getBalance(), "Source account balance should be updated");
        assertEquals(new BigDecimal("600.00"), updatedDestinationAccount.getBalance(), "Destination account balance should be updated");
    }
}
