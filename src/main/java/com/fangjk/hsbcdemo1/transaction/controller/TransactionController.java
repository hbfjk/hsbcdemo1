/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.transaction.controller;

import com.fangjk.hsbcdemo1.exception.ErrorResponse;
import com.fangjk.hsbcdemo1.transaction.service.TransactionService;
import java.math.BigDecimal;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestParam String sourceAccountNumber, 
                           @RequestParam String destinationAccountNumber, 
                           @RequestParam BigDecimal amount) {
        try {
            transactionService.processTransaction(sourceAccountNumber, destinationAccountNumber, amount);
            return ResponseEntity.ok("Transaction processed successfully");
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Transaction failed",
                    Collections.singletonList(ex.getMessage()));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);  // 返回 400 状态码
        }
    }
}