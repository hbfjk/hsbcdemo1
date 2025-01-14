/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.repository;

import com.fangjk.hsbcdemo1.transaction.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    // 使用数据库悲观锁，防止并发修改账户余额
    @Query(value = "SELECT * FROM hsbcdemo.account a WHERE a.account_number = ?1 FOR UPDATE", nativeQuery = true)
    Optional<Account> findByAccountNumberForUpdate(String accountNumber);
}
