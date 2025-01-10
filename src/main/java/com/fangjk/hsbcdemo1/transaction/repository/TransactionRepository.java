/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.repository;

import com.fangjk.hsbcdemo1.transaction.model.Transaction;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t from Transaction t where t.sourceAccountNumber = :sourceAccountNumber")
    List<Transaction> findBySourceAccountNumber(@Param("sourceAccountNumber")String sourceAccountNumber);

    List<Transaction> findByDestinationAccountNumber(String destinationAccountNumber);

    Transaction findByTransactionId(String transactionId);
}
