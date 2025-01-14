/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.controller;

import com.fangjk.hsbcdemo1.transaction.model.Account;
import com.fangjk.hsbcdemo1.transaction.model.Transaction;
import com.fangjk.hsbcdemo1.transaction.repository.AccountRepository;
import com.fangjk.hsbcdemo1.transaction.repository.TransactionRepository;
import com.fangjk.hsbcdemo1.transaction.service.AccountService;
import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mocks")
public class MockController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @PostMapping("/mock")
    public ResponseEntity<?> mock(@RequestParam int accountNumber, 
                           @RequestParam int transactionNumber) {
        List<Account> accounts = generateAccounts(accountNumber);
        List<Transaction> transactions = generateTransactions(accounts, transactionNumber);
        
        accountRepository.saveAll(accounts);
        transactionRepository.saveAll(transactions);
        
        return ResponseEntity.ok("Mock finished successfully");
    }
    
    private final Faker faker = new Faker();

    // Generate random accounts
    public List<Account> generateAccounts(int numberOfAccounts) {
        List<Account> accounts = new ArrayList<>();

        for (int i = 0; i < numberOfAccounts; i++) {
            Account account = new Account();
            account.setAccountNumber("mock-account-"+i);
            //account.setAccountNumber(faker.finance().creditCard());
            account.setBalance(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 5000))); // Random balance
            accounts.add(account);
        }

        return accounts;
    }

    // Generate random transactions
    public List<Transaction> generateTransactions(List<Account> accounts, int numberOfTransactions) {
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < numberOfTransactions; i++) {
            Account sourceAccount = accounts.get(faker.number().numberBetween(0, accounts.size()));
            Account destinationAccount = accounts.get(faker.number().numberBetween(0, accounts.size()));

            // Avoid the same source and destination account
            while (sourceAccount.equals(destinationAccount)) {
                destinationAccount = accounts.get(faker.number().numberBetween(0, accounts.size()));
            }

            Transaction transaction = new Transaction();
            transaction.setTransactionId(faker.idNumber().valid());
            transaction.setSourceAccountNumber(sourceAccount.getAccountNumber());
            transaction.setDestinationAccountNumber(destinationAccount.getAccountNumber());
            transaction.setAmount(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 1000)));  // Random transaction amount
            transaction.setTimestamp(LocalDateTime.now());

            transactions.add(transaction);
        }

        return transactions;
    }
}