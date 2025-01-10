/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.repository;

import com.fangjk.hsbcdemo1.transaction.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    // 使用数据库悲观锁，防止并发修改账户余额
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByAccountNumberForUpdate(String accountNumber);
}
