/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.service;

import com.fangjk.hsbcdemo1.transaction.model.Account;
import com.fangjk.hsbcdemo1.transaction.model.Transaction;
import com.fangjk.hsbcdemo1.transaction.repository.AccountRepository;
import com.fangjk.hsbcdemo1.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // 处理交易
    @Transactional
    @Retryable(value = {RuntimeException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public boolean processTransaction(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        Optional<Account> sourceAccountOpt = accountRepository.findByAccountNumberForUpdate(sourceAccountNumber);
        Optional<Account> destinationAccountOpt = accountRepository.findByAccountNumberForUpdate(destinationAccountNumber);
        
        if (!sourceAccountOpt.isPresent()) {
            throw new RuntimeException("Account not found");
        }
        if (!destinationAccountOpt.isPresent()) {
            throw new RuntimeException("Account not found");
        }
        
        Account sourceAccount = sourceAccountOpt.get();
        Account destinationAccount = destinationAccountOpt.get();
        
        int valid = amount.compareTo(sourceAccount.getBalance());
        if(valid > 0) {
             throw new RuntimeException("Insufficient balance");
        }

        // 更新账户余额
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        // 保存账户变更
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // 创建交易记录
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setSourceAccountNumber(sourceAccountNumber);
        transaction.setDestinationAccountNumber(destinationAccountNumber);
        transaction.setAmount(amount);
        transaction.setTimestamp(java.time.LocalDateTime.now());

        transactionRepository.save(transaction);
        
        return true;
    }
}
