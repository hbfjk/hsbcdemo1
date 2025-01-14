/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.controller;

import com.fangjk.hsbcdemo1.exception.ErrorResponse;
import com.fangjk.hsbcdemo1.transaction.model.Account;
import com.fangjk.hsbcdemo1.transaction.service.AccountService;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author fangj
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    /**
     * Get account balance by account number.
     *
     * @param accountNumber The account number to query.
     * @return The balance of the account.
     */
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getAccountBalance(@PathVariable String accountNumber) {
        try {
            Account account = accountService.getAccountByNumber(accountNumber);
            return ResponseEntity.ok(account.getBalance());
        } catch (IllegalArgumentException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Account not found",
                    Collections.singletonList(ex.getMessage()));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);  // 返回 400 状态码
        }
    }
    
}
