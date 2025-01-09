package com.example.transaction.management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.transaction.management.model.Transaction;

@SpringBootTest
public class TransactionServiceTests {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void testAddTransaction() {
        Transaction transaction = new Transaction();
        transaction.setFromAccountId("123");
        transaction.setToAccountId("456");
        transaction.setAmount(100.0);
        transaction.setCurrency("USD");

        Transaction createdTransaction = transactionService.addTransaction(transaction);
        assertNotNull(createdTransaction);
        assertEquals(100.0, createdTransaction.getAmount());
    }

    // 更多测试用例...
}