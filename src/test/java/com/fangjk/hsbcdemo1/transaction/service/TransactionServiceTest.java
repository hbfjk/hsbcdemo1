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
import org.springframework.boot.test.context.SpringBootTest;

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

    @InjectMocks
    private TransactionService transactionService;
    
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

        when(accountRepository.findByAccountNumberForUpdate("source")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumberForUpdate("destination")).thenReturn(Optional.of(destinationAccount));

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
        when(accountRepository.findByAccountNumberForUpdate("source")).thenReturn(Optional.empty());
        when(accountRepository.findByAccountNumberForUpdate("destination")).thenReturn(Optional.of(destinationAccount));

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
        when(accountRepository.findByAccountNumberForUpdate("source")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumberForUpdate("destination")).thenReturn(Optional.of(destinationAccount));

        // Expecting exception due to insufficient funds
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.processTransaction(sourceAccountNumber,destination,ammount);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }
    
}
