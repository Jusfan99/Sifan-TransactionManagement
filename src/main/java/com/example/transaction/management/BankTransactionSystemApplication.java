package com.example.transaction.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BankTransactionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankTransactionSystemApplication.class, args);
    }
}